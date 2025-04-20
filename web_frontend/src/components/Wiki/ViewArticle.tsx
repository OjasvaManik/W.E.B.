import React, { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import apiService from "../../services/apiService";
import parse from 'html-react-parser';
import { marked } from 'marked';

// Define TypeScript interfaces
interface ArticleResponse {
    articleId: string;
    articleImageUrl: string | null;
    articleTitle: string;
    articleContent: string;
    articleSource: string;
    articleStatus: {
        status: string;
    };
    user: {
        userId: string;
        firstName: string;
        lastName: string;
        role: string;
        userName: string;
    };
    categoryName?: string[]; // This might be included if available
}

const ViewArticle: React.FC = () => {
    const { articleId } = useParams<{ articleId: string }>();
    const navigate = useNavigate();
    const [article, setArticle] = useState<ArticleResponse | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [htmlContent, setHtmlContent] = useState<string>("");

    // Get current user info from localStorage
    const currentUserId = localStorage.getItem("userId");
    const currentUserRole = localStorage.getItem("userRole");

    useEffect(() => {
        fetchArticle();
    }, [articleId]);

    useEffect(() => {
        if (article?.articleContent) {
            // Convert markdown to HTML
            const html = marked(article.articleContent, {
                breaks: true,
                gfm: true
            });
            setHtmlContent(html);
        }
    }, [article]);

    const fetchArticle = async () => {
        setIsLoading(true);
        try {
            const response = await apiService.get<ArticleResponse>(`/wiki/view/${articleId}`);
            setArticle(response);
        } catch (err) {
            console.error("Error fetching article:", err);
            setError("Failed to load article. It may have been removed or you don't have permission to view it.");
        } finally {
            setIsLoading(false);
        }
    };

    // Check if current user can edit the article
    const canEdit = () => {
        if (!article || !currentUserId) return false;

        // Admin can edit any article
        if (currentUserRole === "ADMIN") return true;

        // Article author can edit their own article
        return article.user.userId === currentUserId;
    };

    // Handle edit button click
    const handleEditClick = () => {
        navigate(`/wiki/${articleId}/edit`);
    };

    // Format author name
    const getAuthorName = (article: ArticleResponse) => {
        if (article.user.firstName && article.user.lastName) {
            return `${article.user.firstName} ${article.user.lastName}`;
        }
        return article.user.userName;
    };

    return (
        <div className="container mx-auto p-4 md:p-6">
            {isLoading ? (
                <div className="flex justify-center items-center py-12">
                    <p className="text-gray-500">Loading article...</p>
                </div>
            ) : error ? (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                    <p>{error}</p>
                    <div className="mt-4">
                        <Link to="/wiki" className="text-teal-700 hover:underline">
                            Return to Wiki
                        </Link>
                    </div>
                </div>
            ) : article ? (
                <div className="bg-white rounded-lg shadow-md overflow-hidden">
                    {/* Article Header */}
                    <div className="p-6 border-b">
                        <div className="flex justify-between items-start mb-6">
                            <h1 className="text-3xl font-bold text-gray-800">{article.articleTitle}</h1>

                            {canEdit() && (
                                <button
                                    onClick={handleEditClick}
                                    className="px-4 py-2 bg-teal-700 text-white font-medium rounded hover:bg-teal-800"
                                >
                                    Edit Article
                                </button>
                            )}
                        </div>

                        {/* Author and Categories Info */}
                        <div className="flex flex-col sm:flex-row sm:justify-between text-sm text-gray-600 mb-4">
                            <div>
                                Author: <span className="font-medium">{getAuthorName(article)}</span>
                            </div>

                            {article.categoryName && article.categoryName.length > 0 && (
                                <div className="mt-2 sm:mt-0">
                                    Categories:
                                    <div className="inline-flex flex-wrap gap-1 ml-2">
                                        {article.categoryName.map((category, index) => (
                                            <span key={index} className="bg-gray-100 px-2 py-1 rounded text-gray-700">
                                                {category}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* Source */}
                        {article.articleSource && article.articleSource !== "N/A" && (
                            <div className="text-sm text-gray-500">
                                Source: {article.articleSource}
                            </div>
                        )}
                    </div>

                    {/* Article Content with enhanced image support */}
                    <div className="p-6 prose max-w-none text-gray-600">
                        <div className="markdown-content">
                            {parse(htmlContent)}
                        </div>
                    </div>

                    {/* Back Button */}
                    <div className="p-6 bg-gray-50 border-t">
                        <Link
                            to="/wiki"
                            className="text-teal-700 hover:underline font-medium"
                        >
                            &larr; Back to Wiki
                        </Link>
                    </div>
                </div>
            ) : (
                <div className="text-center py-12">
                    <p className="text-gray-500">Article not found.</p>
                    <div className="mt-4">
                        <Link to="/wiki" className="text-teal-700 hover:underline">
                            Return to Wiki
                        </Link>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ViewArticle;