import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import apiService from '../../services/apiService';

// Define TypeScript interfaces
interface Post {
    postId: string;
    postTitle: string;
    postContent: string;
    userId: string;
    userName: string;
    commentCount: number;
    upVotes: number;
    downVotes: number;
}

const Forum: React.FC = () => {
    const [posts, setPosts] = useState<Post[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');

    // Fetch posts when component mounts
    useEffect(() => {
        fetchPosts();
    }, []);

    const fetchPosts = async () => {
        setIsLoading(true);
        try {
            const response = await apiService.get<Post[]>('/forum');
            setPosts(response);
        } catch (err) {
            console.error('Error fetching posts:', err);
            setError('Failed to load posts. Please try again later.');
        } finally {
            setIsLoading(false);
        }
    };

    // Truncate content for preview
    const truncateContent = (content: string, maxLength: number = 150) => {
        if (content.length <= maxLength) return content;
        return content.substring(0, maxLength) + '...';
    };

    // Strip markdown formatting for preview
    const stripMarkdown = (content: string) => {
        return content
            .replace(/#+\s/g, '') // Remove headings
            .replace(/\*\*/g, '') // Remove bold
            .replace(/\*/g, '')   // Remove italic
            .replace(/```[\s\S]*?```/g, '[code]') // Replace code blocks
            .replace(/---/g, '')  // Remove horizontal rules
            .replace(/\[.*?\]\(.*?\)/g, '') // Remove links
            .replace(/^\s*[\-\*]\s/gm, ''); // Remove list items
    };

    // Get user authentication status
    const isLoggedIn = !!localStorage.getItem('jwtToken');

    return (
        <div className="container mx-auto p-4 md:p-6">
            <div className="flex flex-col md:flex-row justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-teal-700 mb-4 md:mb-0">Forum Discussions</h1>

                <div className="flex items-center">
                    {/* Create post button - only if logged in */}
                    {isLoggedIn && (
                        <Link
                            to="/forum/create"
                            className="px-4 py-2 bg-teal-700 text-white font-medium rounded hover:bg-teal-800"
                        >
                            Create Post
                        </Link>
                    )}
                </div>
            </div>

            {/* Error message */}
            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    {error}
                </div>
            )}

            {/* Loading indicator */}
            {isLoading ? (
                <div className="flex justify-center py-10">
                    <p className="text-gray-500">Loading posts...</p>
                </div>
            ) : (
                <>
                    {/* No posts found */}
                    {posts.length === 0 ? (
                        <div className="bg-gray-50 rounded-lg p-8 text-center">
                            <p className="text-gray-500">No posts found.</p>
                            {isLoggedIn && (
                                <p className="text-gray-500 mt-2">Be the first to create a post!</p>
                            )}
                        </div>
                    ) : (
                        /* Post list */
                        <div className="space-y-6">
                            {posts.map((post) => (
                                <Link
                                    to={`/forum/${post.postId}`}
                                    key={post.postId}
                                    className="block bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-all duration-300 ease-in-out"
                                >
                                    <div className="p-5">
                                        <h2 className="text-xl font-semibold text-gray-800 mb-2">
                                            {post.postTitle}
                                        </h2>

                                        <p className="text-gray-600 mb-4">
                                            {truncateContent(stripMarkdown(post.postContent))}
                                        </p>

                                        <div className="flex justify-between items-center text-sm">
                                            <div className="text-gray-500">
                                                Posted by: <span className="font-medium">{post.userName}</span>
                                            </div>

                                            <div className="flex space-x-4">
                                                <div className="flex items-center space-x-1">
                                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 11l5-5m0 0l5 5m-5-5v12" />
                                                    </svg>
                                                    <span className="text-gray-500">{post.upVotes}</span>
                                                </div>

                                                <div className="flex items-center space-x-1">
                                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 13l-5 5m0 0l-5-5m5 5V6" />
                                                    </svg>
                                                    <span className="text-gray-500">{post.downVotes}</span>
                                                </div>

                                                <div className="flex items-center space-x-1">
                                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                                                    </svg>
                                                    <span className="text-gray-500">{post.commentCount}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </Link>
                            ))}
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default Forum;