import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import apiService from '../../services/apiService';

// Define TypeScript interfaces
interface ArticleStatus {
    status: 'APPROVED' | 'REJECTED' | 'PENDING';
}

interface Article {
    articleId: string;
    articleTitle: string;
    articleContent: string;
    articleSource: string;
    userId: string;
    categoryName: string[];
    articleStatus: ArticleStatus;
}

interface Category {
    categoryId: string;
    categoryName: string;
    categoryDescription: string;
}

const Wiki: React.FC = () => {
    const [articles, setArticles] = useState<Article[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isCategoriesLoading, setIsCategoriesLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');
    const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

    // Fetch articles and categories when component mounts
    useEffect(() => {
        fetchArticles();
        fetchCategories();
    }, []);

    const fetchArticles = async () => {
        setIsLoading(true);
        try {
            const response = await apiService.get<Article[]>('/wiki');
            // Filter to show only approved articles
            const approvedArticles = response.filter(article => article.articleStatus.status === 'APPROVED');
            setArticles(approvedArticles);
        } catch (err) {
            console.error('Error fetching articles:', err);
            setError('Failed to load articles. Please try again later.');
        } finally {
            setIsLoading(false);
        }
    };

    const fetchCategories = async () => {
        setIsCategoriesLoading(true);
        try {
            const response = await apiService.get<Category[]>('/wiki/create/categories');
            setCategories(response);
        } catch (err) {
            console.error('Error fetching categories:', err);
            // Not setting an error message here as articles are the main content
        } finally {
            setIsCategoriesLoading(false);
        }
    };

    // Filter articles based on selected category
    const filteredArticles = selectedCategory === 'ALL'
        ? articles
        : articles.filter(article => article.categoryName.includes(
            categories.find(cat => cat.categoryId === selectedCategory)?.categoryName || ''
        ));

    // Truncate content for preview
    const truncateContent = (content: string, maxLength: number = 100) => {
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
                <h1 className="text-2xl font-bold text-teal-700 mb-4 md:mb-0">Wiki Articles</h1>

                <div className="flex flex-col md:flex-row gap-4 items-center">
                    {/* Category filter dropdown */}
                    <div>
                        {isCategoriesLoading ? (
                            <p className="text-sm text-gray-500">Loading categories...</p>
                        ) : (
                            <select
                                value={selectedCategory}
                                onChange={(e) => setSelectedCategory(e.target.value)}
                                className="p-2 border rounded text-gray-600 bg-white focus:outline-none focus:ring-2 focus:ring-teal-500"
                            >
                                <option value="ALL">All Categories</option>
                                {categories.map(category => (
                                    <option key={category.categoryId} value={category.categoryId}>
                                        {category.categoryName}
                                    </option>
                                ))}
                            </select>
                        )}
                    </div>

                    {/* Create article button - only if logged in */}
                    {isLoggedIn && (
                        <Link
                            to="/wiki/create"
                            className="px-4 py-2 bg-teal-700 text-white font-medium rounded hover:bg-teal-800"
                        >
                            Create Article
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
                    <p className="text-gray-500">Loading articles...</p>
                </div>
            ) : (
                <>
                    {/* No articles found */}
                    {filteredArticles.length === 0 ? (
                        <div className="bg-gray-50 rounded-lg p-8 text-center">
                            <p className="text-gray-500">No articles found.</p>
                            {selectedCategory !== 'ALL' && (
                                <p className="text-gray-500 mt-2">Try selecting a different category.</p>
                            )}
                        </div>
                    ) : (
                        /* Article list */
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {filteredArticles.map((article) => (
                                <Link
                                    to={`/wiki/${article.articleId}`}
                                    key={article.articleId}
                                    className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg hover:scale-105 transition-all duration-300 ease-in-out
"
                                >
                                    <div className="p-5">
                                        <h2 className="text-xl font-semibold text-gray-800 mb-2 line-clamp-2">
                                            {article.articleTitle}
                                        </h2>

                                        <div className="mb-3 text-sm flex flex-wrap gap-1">
                                            {article.categoryName.map((category, index) => (
                                                <span key={index} className="bg-gray-100 px-2 py-1 rounded text-gray-700">
                          {category}
                        </span>
                                            ))}
                                        </div>

                                        <p className="text-gray-600 text-sm mb-4 line-clamp-3">
                                            {truncateContent(stripMarkdown(article.articleContent))}
                                        </p>

                                        <div className="text-xs text-gray-500">
                                            Source: {article.articleSource}
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

export default Wiki;