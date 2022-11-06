package com.example.shoplapttop.service;

import com.example.shoplapttop.model.request.comment.CommentSaveRequest;
import com.example.shoplapttop.model.response.comment.CommentResponse;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    void insertComment(HttpServletRequest request, long productId, CommentSaveRequest commentSaveRequest);

    Page<CommentResponse> getAllComment(int offset, int limit, long productId);
}
