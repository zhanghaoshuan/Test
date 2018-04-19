package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by Administrator on 2018/4/8.
 */

public interface IProductService {
     ServerResponse SaveOrUpdateProduct(Product product);
     ServerResponse<String> setSaleStatus(Integer productId,Integer status);
     ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
     ServerResponse<PageInfo> getProductList(int pageNum,int pageSize);
     ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
     ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
     ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
