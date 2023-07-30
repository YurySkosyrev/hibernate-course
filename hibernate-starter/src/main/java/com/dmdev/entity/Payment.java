package com.dmdev.entity;

import com.dmdev.listener.AuditDatesListener;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Cache;

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
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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