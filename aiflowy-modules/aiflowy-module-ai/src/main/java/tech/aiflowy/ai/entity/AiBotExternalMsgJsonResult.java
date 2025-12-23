//package tech.aiflowy.ai.entity;
//
//import com.alibaba.fastjson.annotation.JSONField;
//
//import java.util.List;
//
//public class AiBotExternalMsgJsonResult {
//
//    @JSONField(ordinal = 1)
//    private String id;
//    private String status;
//    @JSONField(ordinal = 2)
//    private long created;
//    private String object;
//    private String model;
//    @JSONField(ordinal = 3)
//    private Choice choices;
//    private Usage usage;
//    private String serviceTier;
//
//    // Getters and Setters
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getObject() {
//        return object;
//    }
//
//    public void setObject(String object) {
//        this.object = object;
//    }
//
//    public long getCreated() {
//        return created;
//    }
//
//    public void setCreated(long created) {
//        this.created = created;
//    }
//
//    public String getModel() {
//        return model;
//    }
//
//    public void setModel(String model) {
//        this.model = model;
//    }
//
//    public Choice getChoices() {
//        return choices;
//    }
//
//    public void setChoices(Choice choices) {
//        this.choices = choices;
//    }
//
//    public Usage getUsage() {
//        return usage;
//    }
//
//    public void setUsage(Usage usage) {
//        this.usage = usage;
//    }
//
//    public String getServiceTier() {
//        return serviceTier;
//    }
//
//    public void setServiceTier(String serviceTier) {
//        this.serviceTier = serviceTier;
//    }
//
//    // Inner Classes
//    public static class Choice {
//        private int index;
//        private Message message;
//        private Object logprobs; // Can be null
//        private String finishReason;
//        private Delta delta;
//        // Getters and Setters
//        public int getIndex() {
//            return index;
//        }
//
//        public void setIndex(int index) {
//            this.index = index;
//        }
//
//        public Delta getDelta() {
//            return delta;
//        }
//
//        public void setDelta(Delta delta) {
//            this.delta = delta;
//        }
//
//        public Message getMessage() {
//            return message;
//        }
//
//        public void setMessage(Message message) {
//            this.message = message;
//        }
//
//        public Object getLogprobs() {
//            return logprobs;
//        }
//
//        public void setLogprobs(Object logprobs) {
//            this.logprobs = logprobs;
//        }
//
//        public String getFinishReason() {
//            return finishReason;
//        }
//
//        public void setFinishReason(String finishReason) {
//            this.finishReason = finishReason;
//        }
//    }
//    public static class Delta{
//
//        private String role;
//
//        private String content;
//
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//
//        public String getRole() {
//            return role;
//        }
//
//        public void setRole(String role) {
//            this.role = role;
//        }
//    }
//    public static class Message {
//        private String role;
//        private String content;
//        private Object refusal; // Can be null
//        private List<Object> annotations; // Can be empty
//
//        // Getters and Setters
//        public String getRole() {
//            return role;
//        }
//
//        public void setRole(String role) {
//            this.role = role;
//        }
//
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//
//        public Object getRefusal() {
//            return refusal;
//        }
//
//        public void setRefusal(Object refusal) {
//            this.refusal = refusal;
//        }
//
//        public List<Object> getAnnotations() {
//            return annotations;
//        }
//
//        public void setAnnotations(List<Object> annotations) {
//            this.annotations = annotations;
//        }
//    }
//
//    public static class Usage {
//        private int promptTokens;
//        private int completionTokens;
//        private int totalTokens;
//        private PromptTokensDetails promptTokensDetails;
//        private CompletionTokensDetails completionTokensDetails;
//
//        // Getters and Setters
//        public int getPromptTokens() {
//            return promptTokens;
//        }
//
//        public void setPromptTokens(int promptTokens) {
//            this.promptTokens = promptTokens;
//        }
//
//        public int getCompletionTokens() {
//            return completionTokens;
//        }
//
//        public void setCompletionTokens(int completionTokens) {
//            this.completionTokens = completionTokens;
//        }
//
//        public int getTotalTokens() {
//            return totalTokens;
//        }
//
//        public void setTotalTokens(int totalTokens) {
//            this.totalTokens = totalTokens;
//        }
//
//        public PromptTokensDetails getPromptTokensDetails() {
//            return promptTokensDetails;
//        }
//
//        public void setPromptTokensDetails(PromptTokensDetails promptTokensDetails) {
//            this.promptTokensDetails = promptTokensDetails;
//        }
//
//        public CompletionTokensDetails getCompletionTokensDetails() {
//            return completionTokensDetails;
//        }
//
//        public void setCompletionTokensDetails(CompletionTokensDetails completionTokensDetails) {
//            this.completionTokensDetails = completionTokensDetails;
//        }
//    }
//
//    public static class PromptTokensDetails {
//        private int cachedTokens;
//        private int audioTokens;
//
//        // Getters and Setters
//        public int getCachedTokens() {
//            return cachedTokens;
//        }
//
//        public void setCachedTokens(int cachedTokens) {
//            this.cachedTokens = cachedTokens;
//        }
//
//        public int getAudioTokens() {
//            return audioTokens;
//        }
//
//        public void setAudioTokens(int audioTokens) {
//            this.audioTokens = audioTokens;
//        }
//    }
//
//    public static class CompletionTokensDetails {
//        private int reasoningTokens;
//        private int audioTokens;
//        private int acceptedPredictionTokens;
//        private int rejectedPredictionTokens;
//
//        // Getters and Setters
//        public int getReasoningTokens() {
//            return reasoningTokens;
//        }
//
//        public void setReasoningTokens(int reasoningTokens) {
//            this.reasoningTokens = reasoningTokens;
//        }
//
//        public int getAudioTokens() {
//            return audioTokens;
//        }
//
//        public void setAudioTokens(int audioTokens) {
//            this.audioTokens = audioTokens;
//        }
//
//        public int getAcceptedPredictionTokens() {
//            return acceptedPredictionTokens;
//        }
//
//        public void setAcceptedPredictionTokens(int acceptedPredictionTokens) {
//            this.acceptedPredictionTokens = acceptedPredictionTokens;
//        }
//
//        public int getRejectedPredictionTokens() {
//            return rejectedPredictionTokens;
//        }
//
//        public void setRejectedPredictionTokens(int rejectedPredictionTokens) {
//            this.rejectedPredictionTokens = rejectedPredictionTokens;
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "ChatCompletionResponse{" +
//                "id='" + id + '\'' +
//                ", object='" + object + '\'' +
//                ", created=" + created +
//                ", model='" + model + '\'' +
//                ", choices=" + choices +
//                ", usage=" + usage +
//                ", serviceTier='" + serviceTier + '\'' +
//                '}';
//    }
//}