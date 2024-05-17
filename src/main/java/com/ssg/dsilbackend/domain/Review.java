package com.ssg.dsilbackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "review_content", length = 200, nullable = false)
    private String content;

    @Column(name = "review_register_date", nullable = false)
    private LocalDate registerDate;

    @Column(name = "review_score", nullable = false)
    private Long score;

    @Column(name = "review_delete_status", nullable = false)
    private boolean deleteStatus;

    @Column(name = "review_img", length = 500)
    private String img;

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public void setRestaurantReviewDeleteStatus(boolean deleteStatus) {
        this.deleteStatus = deleteStatus;
    }


    public void setDeleteStatus(boolean del) {
        this.deleteStatus = true;

    }


    public void setReplyNUll() {
        this.reply = null;
    }

}

