package com.ctvit.framework.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

public class BaseExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    protected boolean exists;

    protected boolean unableNull;
    
    protected Class<?> clazz;
    
    public BaseExample(Class<?> clazz) {
        this(clazz, true);
    }
    
    public BaseExample(Class<?> clazz, boolean exists) {
        this(clazz, exists, false);
    }
    
    public BaseExample(Class<?> clazz, boolean exists, boolean unableNull) {
    	this.clazz = clazz;
    	this.exists = exists;
    	this.unableNull = unableNull;
    	 oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria(clazz, exists, unableNull);
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;
        protected boolean exists;
        protected boolean unableNull;
        protected Class<?> clazz;
        protected Table table;
        protected Column[] columns;
        protected Map<String, Column> map;
        
        protected GeneratedCriteria(Class<?> clazz, boolean exists, boolean unableNull) {
            super();
            this.clazz = clazz;
            this.exists = exists;
            this.unableNull = unableNull;
            criteria = new ArrayList<Criterion>();
            map = new HashMap<String, Column>();
            table = clazz.getAnnotation(Table.class);
            columns = clazz.getAnnotationsByType(Column.class);
            for (Column column : columns) {
            	map.put(column.name(), column);
            	map.put(column.field(), column);
            }
        }
        
        private String getColumn(String property) {
        	Column column = map.get(property);
        	if (Objects.nonNull(column)) {
        		return column.name();
        	} else if (exists) {
        		throw new RuntimeException("当前实体类不包含名为" + property + "的属性!");
        	} else {
        		 return null;
        	}
        }
        
        @SuppressWarnings("unused")
		private String getField(String property) {
        	 Column column = map.get(property);
        	if (Objects.nonNull(column)) {
        		return column.field();
        	} else if (exists) {
        		throw new RuntimeException("当前实体类不包含名为" + property + "的属性!");
        	} else {
        		 return null;
        	}
        }
        
        private boolean containsProperty(String property) {
        	return map.containsKey(property);
        }
        
        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            if (condition.startsWith("null")) {
                return;
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value) {
            if (value == null) {
            	if (unableNull) {
            		throw new RuntimeException("Value for condition cannot be null");
                } else {
                    return;
                }
            }
            criteria.add(new Criterion(condition, value));
        }
        
        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
            	if (unableNull) {
            		throw new RuntimeException("Value for " + property + " cannot be null");
                } else {
                    return;
                }
            }
            if (Objects.isNull(property)) {
                return;
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
            	if (unableNull) {
            		throw new RuntimeException("Between values for " + property + " cannot be null");
                } else {
                    return;
                }
            }
            if (Objects.isNull(property)) {
                return;
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIsNull(String property) {
            addCriterion(getColumn(property) + " IS NULL");
            return (Criteria) this;
        }

        public Criteria andIsNotNull(String property) {
            addCriterion(getColumn(property) + " IS NOT NULL");
            return (Criteria) this;
        }

        public Criteria andEqualTo(String property, Object value) {
            addCriterion(getColumn(property) + " =", value, property);
            return (Criteria) this;
        }

        public Criteria andNotEqualTo(String property, Object value) {
            addCriterion(getColumn(property) + " <>", value, property);
            return (Criteria) this;
        }

        public Criteria andGreaterThan(String property, Object value) {
            addCriterion(getColumn(property) + " >", value, property);
            return (Criteria) this;
        }

        public Criteria andGreaterThanOrEqualTo(String property, Object value) {
            addCriterion(getColumn(property) + " >=", value, property);
            return (Criteria) this;
        }

        public Criteria andLessThan(String property, Object value) {
            addCriterion(getColumn(property) + " <", value, property);
            return (Criteria) this;
        }

        public Criteria andLessThanOrEqualTo(String property, Object value) {
            addCriterion(getColumn(property) + " <=", value, property);
            return (Criteria) this;
        }

        public Criteria andIn(String property, Iterable<?> values) {
            addCriterion(getColumn(property) + " IN", values, property);
            return (Criteria) this;
        }

        public Criteria andNotIn(String property, Iterable<?> values) {
            addCriterion(getColumn(property) + " NOT IN", values, property);
            return (Criteria) this;
        }

        public Criteria andBetween(String property, Object value1, Object value2) {
            addCriterion(getColumn(property) + " BETWEEN", value1, value2, property);
            return (Criteria) this;
        }

        public Criteria andNotBetween(String property, Object value1, Object value2) {
            addCriterion(getColumn(property) + " NOT BETWEEN", value1, value2, property);
            return (Criteria) this;
        }

        public Criteria andLike(String property, String value) {
            addCriterion(getColumn(property) + " LIKE", value, property);
            return (Criteria) this;
        }

        public Criteria andNotLike(String property, String value) {
            addCriterion(getColumn(property) + " NOT LIKE", value, property);
            return (Criteria) this;
        }
        
        public Criteria andCondition(String condition) {
            addCriterion(condition);
            return (Criteria) this;
        }
        
        public Criteria andCondition(String condition, Object value) {
        	addCriterion(condition, value);
            return (Criteria) this;
        }
        
        public Criteria andAllEqualTo(Object param) {
            MetaObject metaObject = SystemMetaObject.forObject(param);
            String[] properties = metaObject.getGetterNames();
            for (String property : properties) {
                if (containsProperty(property)) {
                    Object value = metaObject.getValue(property);
                    if (Objects.nonNull(value)) {
                        andEqualTo(property, value);
                    } else {
                        andIsNull(property);
                    }
                }
            }
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria(Class<?> clazz, boolean exists, boolean enableNull) {
            super(clazz, exists, enableNull);
        }
        
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}