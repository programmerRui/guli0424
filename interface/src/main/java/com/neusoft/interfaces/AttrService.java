package com.neusoft.interfaces;

import com.neusoft.bean.po.*;

import java.util.List;


public interface AttrService {

    List<BaseCatalog1> getCatalog1();

    List<BaseCatalog2> getCatalog2(String catalog1Id);

    List<BaseCatalog3> getCatalog3(String catalog2Id);

    List<BaseAttrInfo> getAttrInfo(String catalog3Id);

    void saveAttr(BaseAttrInfo baseAttrInfo);

    int deleteAttr(String attrId);

    List<BaseAttrValue> selectAttrValue(String attrId);

    List<BaseAttrInfo> getAttrListByCtg3Id(Long catalog3Id);
}
