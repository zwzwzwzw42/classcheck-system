package com.classcheck.dto;

import com.classcheck.entity.DiscussionMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 讨论消息展示对象：附带发送人姓名与角色（join sys_user 得到）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiscussionMessageVO extends DiscussionMessage {

    private String senderName;

    /** 1学生 2教师 3管理员 */
    private Integer senderRole;
}
