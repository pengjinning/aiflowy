# Milvus 向量数据库

## 1. 安装 DockerDesktop
这里介绍 windows 使用 dockerDesktop 部署 Milvus 向量数据库

1. 安装 dockerDesktop , 点开连接，直接点击 Download for Windows 即可下载
   https://docs.docker.com/desktop/setup/install/windows-install/

![install_dockerDesktop.png](../../../development/ai/resource/install_dockerDesktop.png)

2. 下载好了安装，直接安装即可。安装完了打开 Docker 可视化页面。
   如果页面为以下内容则表示成功(如果页面空白，则需要设置网络，保证能访问docker镜像仓库):

![install_desktop_1.png](../../../development/ai/resource/install_desktop_1.png)

验证是否安装成功
```cmd
#查看docker版本
docker --version
#查看docker-compose版本
docker-compose --version
```

## 2. 部署 Milvus 向量数据库

1. Milvus 下载
   从链接中: 下载选择自己所需的版本即可，这里我选择的是最新版本milvus-2.5.11
   https://github.com/milvus-io/milvus/releases/tag/v2.5.11

![install_milvus_1.png](../../../development/ai/resource/install_milvus_1.png)

在你的本地建立一个 milvus 文件夹，将下载好的文件拷贝至刚刚创建的 milvus下，并改名为：docker-compose.yml。记得一定要改名，不然会报错。

![install_milvus_2.png](../../../development/ai/resource/install_milvus_2.png)

2. Milvus 启动与验证
   打开 cmd 命令行，进入 docker-compose.yml 文件所在的目录。
   输入命令： docker compose up -d，这里记得设置自己的网络，不然加载不了。

![install_milvus_3.png](../../../development/ai/resource/install_milvus_3.png)

## 3. Milvus 图形化界面attu的安装

1. attu 下载
   大家可以点击下载 attu 选择自己所需的版本，我使用的为最新版本 Release v2.4.6

https://github.com/zilliztech/attu/releases/tag/v2.5.8

![install_attu.png](../../../development/ai/resource/install_attu.png)

2. attu 安装
   下载安装文件后，直接安装就行。安装后打开的页面是这样：

![install_attu_1.png](../../../development/ai/resource/install_attu_1.png)

## 4. Milvus 向量数据库配置
**Milvus 向量数据库配置**
1. 以下为未设置密码的配置样例：

![milvus_config_no_pwd.png](../../../development/ai/resource/milvus_config_no_pwd.png)

2. 以下为设置了密码的配置样例：

![milvus_config_pwd.png](../../../development/ai/resource/milvus_config_pwd.png)

**此外 Milvus 还支持更多配置参数**

databaseName = "default" 数据库集合名称，默认为default

token ：使用 token 认证，默认为空，为空则不使用 token 认证
