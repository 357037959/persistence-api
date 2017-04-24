package com.ctvit.framework.core.mapper;

import com.ctvit.framework.core.mapper.ids.DeleteByIdsMapper;
import com.ctvit.framework.core.mapper.ids.SelectByIdsMapper;

/**
 * 通用Mapper接口,根据ids操作
 *
 * @param <T> 不能为空
 * @author liuzh
 */
public interface IdsMapper<T> extends SelectByIdsMapper<T>, DeleteByIdsMapper<T> {

}
