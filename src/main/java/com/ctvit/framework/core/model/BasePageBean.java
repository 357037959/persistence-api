package com.ctvit.framework.core.model;

import org.apache.ibatis.session.RowBounds;

public class BasePageBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_PAGE_SIZE = 20;

	private Integer offset;
	private Integer pageNumber = 1;
	private Integer pageSize;

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public RowBounds getRowBounds() {
		int limit = (pageSize == null ? DEFAULT_PAGE_SIZE : pageSize);
		int off = 0;
		if (offset != null) {
			off = offset;
		} else if (pageNumber != null) {
			off = (pageNumber - 1) * limit;
		}
		RowBounds ret = new RowBounds(off, limit);
		return ret;
	}
}
