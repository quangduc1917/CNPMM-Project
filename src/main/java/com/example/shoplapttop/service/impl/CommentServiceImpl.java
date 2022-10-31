package com.example.shoplapttop.service.impl;

import com.example.shoplapttop.entity.CommentSection;
import com.example.shoplapttop.entity.Product;
import com.example.shoplapttop.entity.User;
import com.example.shoplapttop.exception.ResourceNotFoundException;
import com.example.shoplapttop.mapper.comment.CommentRequestMapper;
import com.example.shoplapttop.mapper.comment.CommentResponseMapper;
import com.example.shoplapttop.model.request.comment.CommentSaveRequest;
import com.example.shoplapttop.model.response.comment.CommentResponse;
import com.example.shoplapttop.repository.CommentSectionRepository;
import com.example.shoplapttop.repository.ProductRepository;
import com.example.shoplapttop.repository.UserRepository;
import com.example.shoplapttop.security.JwtTokenProvider;
import com.example.shoplapttop.service.CommentService;
import com.example.shoplapttop.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentSectionRepository commentSectionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentRequestMapper commentRequestMapper;
    private final CommentResponseMapper commentResponseMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    @Override
    public void insertComment(HttpServletRequest request, long productId, CommentSaveRequest commentSaveRequest) {
        String token = JwtUtil.getToken(request);
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);

        Optional<User> findUser = userRepository.findById(userId);
        findUser.orElseThrow(() -> new UsernameNotFoundException("Don't find user by id = " + userId));

        Optional<Product> resultProduct = productRepository.findById(productId);
        resultProduct.orElseThrow(()->new ResourceNotFoundException("Id not found!","ID", productId));

        CommentSection commentSection = commentRequestMapper.to(commentSaveRequest);

        commentSection.setUserComment(findUser.get());
        commentSection.setProductComment(resultProduct.get());
        commentSection.setCreateDate(LocalDateTime.now());

        commentSectionRepository.save(commentSection);
    }

    @Override
    public Page<CommentResponse> getAllComment(int offset, int limit, long productId) {
        PageRequest pageRequest = PageRequest.of(offset,limit);

        Page<CommentSection> commentSections = commentSectionRepository.findAll(new Specification<CommentSection>() {
            @Override
            public Predicate toPredicate(Root<CommentSection> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.conjunction();
                Predicate predicateProduct = criteriaBuilder.equal(root.join("product").get("productId"),productId);
                query.orderBy(criteriaBuilder.desc(root.get("cmtId")));
                return p;
            }
        },pageRequest);

        return commentSections.map(t->{
           CommentResponse commentResponse = commentResponseMapper.to(t);
           commentResponse.setUserName(t.getUserComment().getUserName());
           commentResponse.setImgAvatar(t.getUserComment().getImgAvatar());
           return commentResponse;
        });
    }
}
