package com.hunter.data.controller;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.PostRemove;

import org.springframework.stereotype.Component;

import com.hunter.web.service.DeleteInfoService;

@Component
public class DeleteEventListener {

	@PostRemove
	private void afterAnyUpdate(Object entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		try {
			String className = entity.getClass().getSimpleName();
			Long remoteId = (Long) entity.getClass().getMethod("getRemoteId").invoke(entity);

			if(remoteId != null) {
				DeleteInfoService deleteInfoService = BeanUtil.getBean(DeleteInfoService.class);
				deleteInfoService.storeDeleteInfoInDB(className, remoteId);
			}
		} catch(Exception e) {}

	}

}