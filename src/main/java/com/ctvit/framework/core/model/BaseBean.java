package com.ctvit.framework.core.model;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BaseBean implements Serializable {

	private static final long serialVersionUID = 3663687482168313896L;

//	private BeanPopulator<BaseBean> populator;

//	public BaseBean() {
//		this.populator = (BeanPopulator<BaseBean>) BeanPopulatorFactory.getPopulater(getClass());
//	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	public Map<String, Object> toMap() {
		return BeanPopulatorFactory.getPopulater(getClass()).toMap(this);
	}

	@SuppressWarnings("unchecked")
	public BaseBean toBean(Map<String,Object> map) {
		return ((BeanPopulator<BaseBean>)BeanPopulatorFactory.getPopulater(getClass())).toBean(this, map);
	}
}
