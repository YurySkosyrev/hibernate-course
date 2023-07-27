package com.dmdev.entity;

import com.dmdev.listener.AuditDatesListener;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditDatesListener.class)
@EqualsAndHashCode(callSuper=false)
//@OptimisticLocking(type = OptimisticLockType.ALL)
//@DynamicUpdate
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Audited
public class Payment extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
//    @Version
//    private Long version;

    @Column(nullable = false)
    private Integer amount;

//    @NotAudited
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

}