package com.dmdev.listener;

import com.dmdev.entity.Revision;
import org.hibernate.envers.RevisionListener;

public class DmdevRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        // revisionEntity - наш класс Revision
        // SecurityContext.getUser().getName()
        ((Revision) revisionEntity).setUsername("dmdev");
    }
}
