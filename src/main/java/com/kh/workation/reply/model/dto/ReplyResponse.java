package com.kh.workation.reply.model.dto;

import java.time.LocalDateTime;

public class ReplyResponse {
	
    private Integer replyId;

    private Integer crewId;

    private Integer employeeId;

    private String employeeName;

    private Integer parentReplyId;

    private String replyContent;

    private String replyPrivate;

    private LocalDateTime createDate;

    private String status;

    private boolean canEdit;

    private boolean canDelete;

}
