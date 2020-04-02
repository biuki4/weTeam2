package com.iamk.weTeam.model.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message implements Serializable {
    private static final long serialVersionUID = 8520940847774685836L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_time")
    private Date createTime;

    private String title;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "msg_content")
    private String msgContent;

    @Column(name = "sender_id")
    private Integer senderId;

    @Column(name = "receiver_id")
    private Integer receiverId;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", msgContent='" + msgContent + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                '}';
    }
}