package com.neusoft.interfaces;

import com.neusoft.bean.po.BaseAttrInfo;
import com.neusoft.bean.po.BaseCatalog1;
import com.neusoft.bean.po.BaseCatalog2;
import com.neusoft.bean.po.BaseCatalog3;

import java.util.List;


public interface MangerService {

    List<BaseCatalog1> getCatalog1();

    List<BaseCatalog2> getCatalog2(String catalog1Id);

    List<BaseCatalog3> getCatalog3(String catalog2Id);

    List<BaseAttrInfo> getAttrInfo(String catalog3Id);

    void saveAttr(BaseAttrInfo baseAttrInfo);

    int deleteAttr(String attrId);
}
