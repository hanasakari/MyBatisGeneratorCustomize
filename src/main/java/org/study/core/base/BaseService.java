package org.study.core.base;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by asuka on 3/14/2018.
 * 基于http://blog.csdn.net/yf275908654/article/details/50171607 修改
 * @author S1ow
 */
@Service
public abstract class BaseService<T, E, PK extends Serializable> implements BaseMapper<T, E, PK> {

    @Autowired
    private BaseMapper<T, E, PK> BaseMapper;

    public BaseMapper<T, E, PK> getBaseMapper() {
        return BaseMapper;
    }

    public void setBaseMapper(BaseMapper<T, E, PK> BaseMapper) {
        this.BaseMapper = BaseMapper;
    }

    public int insert(T entity) {
        return getBaseMapper().insert(entity);
    }

    public int insertSelective(T record) {
        return getBaseMapper().insertSelective(record);
    }

    public T selectByPrimaryKey(PK id) {
        return getBaseMapper().selectByPrimaryKey(id);
    }

    public int updateByPrimaryKey(T record) {
        return getBaseMapper().updateByPrimaryKey(record);
    }

    public int updateByPrimaryKeySelective(T record) {
        return getBaseMapper().updateByPrimaryKeySelective(record);
    }

    public int deleteByPrimaryKey(PK id) {
        return getBaseMapper().deleteByPrimaryKey(id);
    }


    public long countByExample(E example) {
        return getBaseMapper().countByExample(example);
    }

    public int deleteByExample(E example) {
        return getBaseMapper().deleteByExample(example);
    }

    public List<T> selectByExample(E example) {
        return getBaseMapper().selectByExample(example);
    }

    public int updateByExampleSelective(@Param("record") T record,
                                        @Param("example") E example) {
        return getBaseMapper().updateByExampleSelective(record, example);
    }

    public int updateByExample(@Param("record") T record, @Param("example") E example) {
        return getBaseMapper().updateByExample(record, example);
    }

}
