package com.kh.workation.reply.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyCreateRequest {

    private String replyContent;
    private String replyPrivate;
    private Integer parentReplyId;
}
