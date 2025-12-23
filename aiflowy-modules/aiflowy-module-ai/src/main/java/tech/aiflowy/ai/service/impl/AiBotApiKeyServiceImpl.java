package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.aiflowy.ai.entity.Bot;
import tech.aiflowy.ai.entity.BotApiKey;
import tech.aiflowy.ai.mapper.AiBotApiKeyMapperMapper;
import tech.aiflowy.ai.service.AiBotApiKeyService;
import tech.aiflowy.ai.service.AiBotService;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

//import static tech.aiflowy.ai.entity.table.AiBotApiKeyTableDef.AI_BOT_API_KEY;

/**
 * bot apiKey 表 服务层实现。
 *
 * @author ArkLight
 * @since 2025-07-18
 */
@Service
public class AiBotApiKeyServiceImpl extends ServiceImpl<AiBotApiKeyMapperMapper, BotApiKey>  implements AiBotApiKeyService {

    private static final Logger log = LoggerFactory.getLogger(AiBotApiKeyServiceImpl.class);

    @Resource
    private AiBotService aiBotService;

    @Value("${aiflowy.aiBot.apiKeyMasterKey:Kj9#mP2$nQ4&rT6*uY8@wE1!zX3%vC5^}")
    private String masterKey;

    /**
     * 根据 botId 生成 apiKey 并返回
     */
    @Override
    public String generateApiKeyByBotId(BigInteger botId) {



        Bot aiBot = aiBotService.getById(botId);

        if (aiBot == null){
            log.error("bot 不存在！");
            throw new BusinessException("bot 不存在！");
        }

        if (!StringUtils.hasLength(masterKey) || masterKey.length() != 32){

            log.error("生成 apiKey 失败，主密钥需为 32 字节字符串");

            throw new BusinessException("生成 apiKey 失败");
        }

        try {
            // 生成随机盐 (16字节)
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);

            // 固定密钥
            SecretKeySpec secretKey = new SecretKeySpec(masterKey.getBytes(), "AES");

            // 加密botId
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(salt);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] botIdBytes = botId.toByteArray();
            byte[] encryptedData = cipher.doFinal(botIdBytes);

            // 只对加密数据进行Base64编码作为apiKey
            String apiKey = Base64.getEncoder().encodeToString(encryptedData);

            // 盐单独存储到数据库（Base64编码）
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            log.info("salt:{}",saltBase64);
            log.info("apiKey:{}",apiKey);
            // 存入数据库

            BotApiKey botApiKey = new BotApiKey();
            botApiKey.setApiKey(apiKey);
            botApiKey.setSalt(saltBase64);
            botApiKey.setBotId(botId.longValue());
            
            save(botApiKey);

            // 返回加密结果
            return apiKey;

        } catch (Exception e) {
            throw new BusinessException("生成API Key失败：" + e.getMessage());
        }


    }


    public BigInteger decryptApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new BusinessException("API Key不能为空");
        }

        try {
            // 从数据库获取盐值
            String saltBase64 = getSaltByApiKey(apiKey);
            if (saltBase64 == null || saltBase64.isEmpty()) {
                log.error("slat校验不通过");
                throw new BusinessException("API Key不存在或已失效");
            }

            // 解码盐值
            byte[] salt = Base64.getDecoder().decode(saltBase64);

            // Base64解码apiKey
            byte[] encryptedData = Base64.getDecoder().decode(apiKey);

            // 固定密钥（与加密时相同）
            SecretKeySpec secretKey = new SecretKeySpec(masterKey.getBytes(), "AES");

            // 解密
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(salt);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);

            // 字节数组转换回BigInteger
            BigInteger botId = new BigInteger(decryptedData);
            log.info(botId + "");
            return botId;

        } catch (Exception e) {
            throw new BusinessException("解密API Key失败：" + e.getMessage());
        }
    }


    private String getSaltByApiKey(String apiKey){
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BotApiKey::getApiKey,apiKey);
        BotApiKey apiKeyFromDB = getOne(queryWrapper);
        if (apiKeyFromDB == null) {
            return "";
        }

        return apiKeyFromDB.getSalt();
    }
}
