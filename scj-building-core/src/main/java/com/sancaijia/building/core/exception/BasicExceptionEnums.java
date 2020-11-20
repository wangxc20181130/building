package com.sancaijia.building.core.exception;

public enum BasicExceptionEnums implements BaseErrorInfoInterface {
    // 数据操作错误定义
    SUCCESS("200", "成功!"),
    BODY_NOT_MATCH("400", "请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH("401", "请求的数字签名不匹配!"),
    NOT_FOUND("404", "未找到该资源!"),
//    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    SERVER_BUSY("503", "服务器正忙，请稍后再试!"),
    INVALID_TOKEN("-1000", "非法的用户凭据，请重新登录"),
    NO_PERMISSION("403", "无操作权限"),
    BAD_REQUEST("400", "非法请求"),
    FIELD_VALIDA_ERROR("-5001", "属性校验失败"),
    SYSTEM_ERROR("500", "系统内部异常"),

    // 业务错误(房源)
    HOUSE_NOT_EXIST("40001", "不存在该房源!!!"),

    // 规则配置错误
    RULES_NOT_EXIST("601", "不存在该配置!!!"),
    RULES_OPERATION("602", "无变化操作!"),

    // 任务相关错误
    TASK_NOT_EXIST("50000", "无此任务"),
    TASK_IS_RUNING("50001","任务已启动，无法再起启动"),
    TASK_IS_PAUSE("50002","任务暂停，只可继续执行"),
    TASK_NOT_RUNING("50003","任务未执行，无法暂停"),
    ;

    /**
     * 错误码
     */
    private String resultCode;

    /**
     * 错误描述
     */
    private String resultMsg;

    BasicExceptionEnums(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
