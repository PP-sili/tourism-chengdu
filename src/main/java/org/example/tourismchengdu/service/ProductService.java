package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.mall.ProductDetailVO;
import org.example.tourismchengdu.dto.mall.ProductSummaryVO;

public interface ProductService {

    PageResult<ProductSummaryVO> listProducts(String category, int page, int size);

    ProductDetailVO getProductDetail(Long id);
}
