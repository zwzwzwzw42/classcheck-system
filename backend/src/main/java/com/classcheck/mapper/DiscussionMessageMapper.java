package com.classcheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classcheck.dto.DiscussionMessageVO;
import com.classcheck.entity.DiscussionMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiscussionMessageMapper extends BaseMapper<DiscussionMessage> {

    /**
     * 分页拉取最近消息（倒序），关联 sys_user 取发送人姓名与角色。
     * Service 层再反转为正序用于展示。
     */
    @Select("""
            SELECT m.id,
                   m.sender_id   AS senderId,
                   m.content,
                   m.image_url   AS imageUrl,
                   m.create_time AS createTime,
                   u.name        AS senderName,
                   u.role        AS senderRole
            FROM discussion_message m
            LEFT JOIN sys_user u ON u.id = m.sender_id
            ORDER BY m.id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<DiscussionMessageVO> selectRecent(@Param("limit") int limit, @Param("offset") int offset);
}
