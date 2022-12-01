package com.hunter;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

public class SeqGen extends SequenceStyleGenerator {

    /**
     * Custom id generation.
     * if id is 'null' or '0' then generate one.
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
    	try {
			Long id = (Long)obj.getClass().getMethod("getId").invoke(obj);
			if (id == null || id == 0) id = (Long) super.generate(session, obj);
			System.out.println("Generated id: " + id);
			return id;
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
        return super.generate(session, obj);
    }
}