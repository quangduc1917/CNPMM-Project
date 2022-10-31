package com.example.shoplapttop.controller;

import com.example.shoplapttop.model.request.comment.CommentSaveRequest;
import com.example.shoplapttop.model.response.ApiResponse;
import com.example.shoplapttop.model.response.comment.CommentResponse;
import com.example.shoplapttop.model.response.review.ReviewResponse;
import com.example.shoplapttop.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/api/comment/add")
    @PreAuthorize("hasAnyRole('USER') OR hasAnyRole('ADMIN')")
    public ResponseEntity<?> commentProduct(HttpServletRequest request, @RequestParam long productId, @RequestBody CommentSaveRequest commentSaveRequest){
        commentService.insertComment(request, productId, commentSaveRequest);
        return new ResponseEntity(new ApiResponse(true,"SUCCESS"), HttpStatus.OK);
    }

    @GetMapping("/api/public/comment/all")
    public ResponseEntity<Page<CommentResponse>> getAllComments(@RequestParam int offset, @RequestParam int limit, @RequestParam long productId){
        return new ResponseEntity<>(commentService.getAllComment(offset, limit, productId),HttpStatus.OK);
    }

}
