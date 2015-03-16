package com.hunnymustard.ssbl.server.repository.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Notification;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.NotificationRepository;

@Repository("notificationRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class NotificationRepositoryHibernate extends HibernateRepository<Notification, Integer> implements NotificationRepository {

	@Override
	@SuppressWarnings("unchecked")
	public List<Notification> findByNew(User user) {
		List<Notification> notifs = (List<Notification>) getSession().createCriteria(Notification.class)
				.add(Restrictions.ne("sender", user))
				.add(Restrictions.gt("sendTime", user.getLastLoginTime()))
				.list();
		
		for(Notification notif : notifs)
			Hibernate.initialize(notif.getSender());
		
		return notifs;
	}

}