package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.mall.ProductDetailVO;
import org.example.tourismchengdu.dto.mall.ProductSummaryVO;
import org.example.tourismchengdu.entity.Product;
import org.example.tourismchengdu.mapper.ProductMapper;
import org.example.tourismchengdu.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public PageResult<ProductSummaryVO> listProducts(String category, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Product> products;
        long total;
        if (StringUtils.hasText(category)) {
            products = productMapper.findVisibleByCategory(category, offset, safeSize);
            total = productMapper.countVisibleByCategory(category);
        } else {
            products = productMapper.findVisible(offset, safeSize);
            total = productMapper.countVisible();
        }

        List<ProductSummaryVO> list = products.stream().map(ProductSummaryVO::from).toList();
        return new PageResult<>(list, total, safePage, safeSize);
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        return ProductDetailVO.from(product);
    }
}
