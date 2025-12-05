package tech.aiflowy.ai.tinyflow.entity;

import dev.tinyflow.core.chain.NodeStatus;
import dev.tinyflow.core.chain.Parameter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NodeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 执行状态
     * @see NodeStatus
     */
    private Integer status;
    /**
     * 消息，错误时显示
     */
    private String message;
    /**
     * 执行结果
     */
    private Map<String, Object> result;
    /**
     * 挂起时需要填写的参数
     */
    private List<Parameter> suspendForParameters;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public List<Parameter> getSuspendForParameters() {
        return suspendForParameters;
    }

    public void setSuspendForParameters(List<Parameter> suspendForParameters) {
        this.suspendForParameters = suspendForParameters;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "nodeId='" + nodeId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", result=" + result +
                ", suspendForParameters=" + suspendForParameters +
                '}';
    }
}
