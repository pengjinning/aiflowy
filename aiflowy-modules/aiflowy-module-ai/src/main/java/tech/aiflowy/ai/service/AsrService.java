package tech.aiflowy.ai.service;

import java.io.InputStream;

public interface AsrService {

    String recognize(InputStream audioStream) throws Exception;

}
