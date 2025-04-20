import React, { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import apiService from "../../services/apiService";
import parse from 'html-react-parser';
import { marked } from 'marked';

// Define TypeScript interfaces
interface PostResponse {
    postId: string;
    postTitle: string;
    postContent: string;
    userId: string;
    userName: string;
    comments: Comment[];
    upVotes: number;
    downVotes: number;
}

interface Comment {
    commentId: string;
    commentContent: string;
    userId: string;
    userName: string;
    // Add other comment fields as needed
}

interface VoteResponse {
    postId: string;
    upVote?: number;
    downVote?: number;
}

interface CommentRequest {
    commentContent: string;
    postId: string;
    userId: string;
}

const ViewPost: React.FC = () => {
    const { postId } = useParams<{ postId: string }>();
    const navigate = useNavigate();
    const [post, setPost] = useState<PostResponse | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [htmlContent, setHtmlContent] = useState<string>("");
    const [isVoting, setIsVoting] = useState<boolean>(false);

    // New state for comment functionality
    const [commentContent, setCommentContent] = useState<string>("");
    const [isSubmittingComment, setIsSubmittingComment] = useState<boolean>(false);
    const [commentError, setCommentError] = useState<string>("");

    // Get current user info from localStorage
    const currentUserId = localStorage.getItem("userId");
    const currentUserName = localStorage.getItem("userName");
    const isLoggedIn = !!localStorage.getItem("jwtToken");

    useEffect(() => {
        fetchPost();
    }, [postId]);

    useEffect(() => {
        if (post?.postContent) {
            // Convert markdown to HTML
            const html = marked(post.postContent, {
                breaks: true,
                gfm: true
            });
            setHtmlContent(html);
        }
    }, [post]);

    const fetchPost = async () => {
        setIsLoading(true);
        try {
            const response = await apiService.get<PostResponse>(`/forum/${postId}`);
            setPost(response);
        } catch (err) {
            console.error("Error fetching post:", err);
            setError("Failed to load post. It may have been removed or you don't have permission to view it.");
        } finally {
            setIsLoading(false);
        }
    };

    // Check if current user can edit the post
    const canEdit = () => {
        if (!post || !currentUserId) return false;
        return post.userId === currentUserId;
    };

    // Handle edit button click
    const handleEditClick = () => {
        navigate(`/forum/${postId}/edit`);
    };

    // Handle upvote
    const handleUpvote = async () => {
        if (!isLoggedIn || !currentUserId || isVoting) return;

        setIsVoting(true);
        try {
            const payload = {
                postId,
                userId: currentUserId
            };

            const response = await apiService.put<VoteResponse>('/forum/post/upVote', payload);

            // Update post with new vote count
            if (post && response.upVote !== undefined) {
                setPost({
                    ...post,
                    upVotes: response.upVote
                });
            }
        } catch (err) {
            console.error("Error upvoting post:", err);
            setError("Failed to upvote. Please try again.");
        } finally {
            setIsVoting(false);
        }
    };

    // Handle downvote
    const handleDownvote = async () => {
        if (!isLoggedIn || !currentUserId || isVoting) return;

        setIsVoting(true);
        try {
            const payload = {
                postId,
                userId: currentUserId
            };

            const response = await apiService.put<VoteResponse>('/forum/post/downVote', payload);

            // Update post with new vote count
            if (post && response.downVote !== undefined) {
                setPost({
                    ...post,
                    downVotes: response.downVote
                });
            }
        } catch (err) {
            console.error("Error downvoting post:", err);
            setError("Failed to downvote. Please try again.");
        } finally {
            setIsVoting(false);
        }
    };

    // Handle comment submission
    const handleCommentSubmit = async () => {
        // Validate comment
        if (!commentContent.trim()) {
            setCommentError("Comment cannot be empty");
            return;
        }

        if (!isLoggedIn || !currentUserId || !postId) {
            setCommentError("You must be logged in to comment");
            return;
        }

        setIsSubmittingComment(true);
        setCommentError("");

        try {
            const commentRequest: CommentRequest = {
                commentContent: commentContent.trim(),
                postId: postId,
                userId: currentUserId
            };

            // Send comment to API
            await apiService.post('/forum/post/comment', commentRequest);

            // Clear comment input
            setCommentContent("");

            // Add the new comment to the post's comments array
            if (post && currentUserName) {
                const newComment: Comment = {
                    commentId: Date.now().toString(), // Temporary ID until refresh
                    commentContent: commentContent.trim(),
                    userId: currentUserId,
                    userName: currentUserName
                };

                setPost({
                    ...post,
                    comments: [...post.comments, newComment]
                });
            }

            // Alternatively, refresh the whole post to get the updated comments
            // fetchPost();
        } catch (err) {
            console.error("Error submitting comment:", err);
            setCommentError("Failed to submit comment. Please try again.");
        } finally {
            setIsSubmittingComment(false);
        }
    };

    return (
        <div className="container mx-auto p-4 md:p-6">
            {isLoading ? (
                <div className="flex justify-center items-center py-12">
                    <p className="text-gray-500">Loading post...</p>
                </div>
            ) : error ? (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                    <p>{error}</p>
                    <div className="mt-4">
                        <Link to="/forum" className="text-teal-700 hover:underline">
                            Return to Forum
                        </Link>
                    </div>
                </div>
            ) : post ? (
                <div className="bg-white rounded-lg shadow-md overflow-hidden">
                    {/* Post Header */}
                    <div className="p-6 border-b">
                        <div className="flex justify-between items-start mb-6">
                            <h1 className="text-3xl font-bold text-gray-800">{post.postTitle}</h1>

                            {canEdit() && (
                                <button
                                    onClick={handleEditClick}
                                    className="px-4 py-2 bg-teal-700 text-white font-medium rounded hover:bg-teal-800"
                                >
                                    Edit Post
                                </button>
                            )}
                        </div>

                        {/* Author Info */}
                        <div className="flex justify-between items-center text-sm text-gray-600 mb-4">
                            <div>
                                Posted by: <span className="font-medium">{post.userName}</span>
                            </div>

                            {/* Voting Buttons */}
                            <div className="flex items-center space-x-4">
                                <div className="flex items-center">
                                    <button
                                        onClick={handleUpvote}
                                        disabled={!isLoggedIn || isVoting}
                                        className={`flex items-center space-x-1 p-1 rounded ${
                                            isLoggedIn ? 'hover:bg-gray-100' : 'cursor-default'
                                        }`}
                                        title={isLoggedIn ? "Upvote" : "Login to vote"}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
                                        </svg>
                                        <span className="font-medium">{post.upVotes}</span>
                                    </button>
                                </div>

                                <div className="flex items-center">
                                    <button
                                        onClick={handleDownvote}
                                        disabled={!isLoggedIn || isVoting}
                                        className={`flex items-center space-x-1 p-1 rounded ${
                                            isLoggedIn ? 'hover:bg-gray-100' : 'cursor-default'
                                        }`}
                                        title={isLoggedIn ? "Downvote" : "Login to vote"}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                        </svg>
                                        <span className="font-medium">{post.downVotes}</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Post Content */}
                    <div className="p-6 prose max-w-none text-gray-600">
                        <div className="markdown-content">
                            {parse(htmlContent)}
                        </div>
                    </div>

                    {/* Comments Section */}
                    <div className="p-6 bg-gray-50 border-t">
                        <h2 className="text-xl font-semibold text-gray-800 mb-4">Comments ({post.comments.length})</h2>

                        {post.comments.length === 0 ? (
                            <p className="text-gray-500">No comments yet.</p>
                        ) : (
                            <div className="space-y-4">
                                {post.comments.map((comment) => (
                                    <div key={comment.commentId} className="bg-white p-4 rounded shadow">
                                        <div className="mb-2 font-medium text-gray-700">{comment.userName}</div>
                                        <div className="text-gray-600">{comment.commentContent}</div>
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* Add Comment Section - Only visible if logged in */}
                        {isLoggedIn ? (
                            <div className="mt-6">
                                <h3 className="text-lg font-medium text-gray-800 mb-3">Add a Comment</h3>
                                {commentError && (
                                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded mb-3">
                                        {commentError}
                                    </div>
                                )}
                                <textarea
                                    className="w-full p-3 border rounded border-black text-gray-600 focus:ring focus:ring-teal-300 focus:border-teal-500"
                                    rows={4}
                                    placeholder="Write your comment here..."
                                    value={commentContent}
                                    onChange={(e) => setCommentContent(e.target.value)}
                                    disabled={isSubmittingComment}
                                />
                                <div className="mt-2">
                                    <button
                                        className="px-4 py-2 bg-teal-700 text-white font-medium rounded hover:bg-teal-800 disabled:bg-gray-400 disabled:cursor-not-allowed"
                                        onClick={handleCommentSubmit}
                                        disabled={isSubmittingComment || !commentContent.trim()}
                                    >
                                        {isSubmittingComment ? "Posting..." : "Post Comment"}
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div className="mt-6 p-4 bg-gray-100 rounded">
                                <p className="text-gray-600">
                                    Please <Link to="/login" className="text-teal-700 hover:underline">log in</Link> to leave a comment.
                                </p>
                            </div>
                        )}
                    </div>

                    {/* Back Button */}
                    <div className="p-6 bg-gray-50 border-t">
                        <Link
                            to="/forum"
                            className="text-teal-700 hover:underline font-medium"
                        >
                            &larr; Back to Forum
                        </Link>
                    </div>
                </div>
            ) : (
                <div className="text-center py-12">
                    <p className="text-gray-500">Post not found.</p>
                    <div className="mt-4">
                        <Link to="/forum" className="text-teal-700 hover:underline">
                            Return to Forum
                        </Link>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ViewPost;