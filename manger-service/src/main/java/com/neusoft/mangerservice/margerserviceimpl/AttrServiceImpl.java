package com.neusoft.mangerservice.margerserviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.*;
import com.neusoft.interfaces.AttrService;
import com.neusoft.mangerservice.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AttrServiceImpl implements AttrService {
    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;
    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(Long.parseLong(catalog1Id));
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(Long.parseLong(catalog2Id));
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfo(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(Long.parseLong(catalog3Id));
        return baseAttrInfoMapper.select(baseAttrInfo);
    }

    @Override
    public void saveAttr(BaseAttrInfo baseAttrInfo) {
        //如果有主键就进行更新，没有就插入
        if(baseAttrInfo.getId()!=null&&baseAttrInfo.getId().toString().length()>0){
            baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        }else{
            //防止主键被赋上一个空字符串
            if(baseAttrInfo.getId()==null||baseAttrInfo.getId().toString().length()==0){
                baseAttrInfo.setId(null);
            }
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        //把原属性值全部清空
        if(baseAttrInfo.getId()!=null){
            BaseAttrValue baseAttrValueDel = new BaseAttrValue();
            baseAttrValueDel.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.delete(baseAttrValueDel);
        }
        //重新插入属性
        if(baseAttrInfo.getAttrValueList()!=null&&baseAttrInfo.getAttrValueList().size()>0){
            for (BaseAttrValue attrValue : baseAttrInfo.getAttrValueList()) {
                //防止主键被赋上一个空字符串
                if(attrValue.getId()==null||attrValue.getId().toString().length()==0){
                    attrValue.setId(null);
                }
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(attrValue);
            }
        }
//        baseAttrInfoMapper.insertSelective(baseAttrInfo);
//        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
//        for (BaseAttrValue baseAttrValue : attrValueList) {
//            baseAttrValue.setAttrId(baseAttrInfo.getId());
//            baseAttrValueMapper.insert(baseAttrValue);
//        }
    }

    @Override
    public int deleteAttr(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrValue.setAttrId(Long.parseLong(attrId));
        baseAttrInfo.setId(Long.parseLong(attrId));
        baseAttrValueMapper.delete(baseAttrValue);
        int num = baseAttrInfoMapper.delete(baseAttrInfo);
        return num;
    }

    @Override
    public List<BaseAttrValue> selectAttrValue(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(Long.parseLong(attrId));
        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public List<BaseAttrInfo> getAttrListByCtg3Id(Long catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.select(baseAttrInfo);
        for (BaseAttrInfo attrInfo : baseAttrInfoList) {
            Long attrId=attrInfo.getId();
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrId);
            List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);
            attrInfo.setAttrValueList(baseAttrValueList);
        }
        return baseAttrInfoList;
    }

    @Override
    public List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds) {
        if(valueIds!=null&&valueIds.size()>0){
            String join = StringUtils.join(valueIds, ",");

            List<BaseAttrInfo> baseAttrInfos = baseAttrValueMapper.selectAttrListByValueIds(join);

            return baseAttrInfos;
        }else{
            return null;
        }

    }


}
