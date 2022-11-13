package keldkemp.telegram.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorServerDto<T extends ErrorServerDto> {
    private String message;
    private String code;
    private String url;
    private UUID guid;
    private String stack;
    private NodeDto node;

    public ErrorServerDto() {
    }

    public ErrorServerDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public T setMessage(String message) {
        this.message = message;
        return (T) this;
    }

    public String getCode() {
        return code;
    }

    public T setCode(String code) {
        this.code = code;
        return (T) this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getGuid() {
        return guid;
    }

    public T setGuid(UUID guid) {
        this.guid = guid;
        return (T) this;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public NodeDto getNode() {
        return node;
    }

    public void setNode(NodeDto node) {
        this.node = node;
    }
}
