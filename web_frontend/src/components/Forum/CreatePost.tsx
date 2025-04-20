import React, { useState, useEffect, useRef } from "react";
import MDEditor from "@uiw/react-md-editor";
import { useNavigate } from "react-router-dom";
import apiService from "../../services/apiService";

// Define TypeScript interfaces
interface PostPayload {
    postTitle: string;
    postContent: string;
    userId: string;
}

interface ImageData {
    id: string;
    name: string;
    dataUrl: string;
}

export default function CreatePost() {
    const navigate = useNavigate();
    const [content, setContent] = useState<string>("");
    const [title, setTitle] = useState<string>("");
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>("");
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [images, setImages] = useState<ImageData[]>([]);
    const editorRef = useRef<any>(null);

    // Check authentication on component mount
    useEffect(() => {
        const jwtToken = localStorage.getItem("jwtToken");
        const userName = localStorage.getItem("userName");

        if (!jwtToken || !userName) {
            // User is not logged in, redirect to login page
            navigate("/login", { state: { from: location.pathname } });
        }
    }, [navigate]);

    // Add paste event listener for the entire document
    useEffect(() => {
        const handlePaste = async (e: ClipboardEvent) => {
            const items = e.clipboardData?.items;
            if (!items) return;

            for (let i = 0; i < items.length; i++) {
                if (items[i].type.indexOf('image') !== -1) {
                    e.preventDefault();
                    const file = items[i].getAsFile();
                    if (file) {
                        handleImageInsert(file);
                    }
                    break;
                }
            }
        };

        document.addEventListener('paste', handlePaste);
        return () => {
            document.removeEventListener('paste', handlePaste);
        };
    }, [content]);

    // Get user ID from localStorage (matching the stored values from login component)
    const userId = localStorage.getItem("userId") || "";
    const userName = localStorage.getItem("userName") || "";

    // Handle file selection
    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const files = e.target.files;
        if (files && files.length > 0) {
            handleImageInsert(files[0]);
            // Reset the input so the same file can be uploaded again if needed
            if (fileInputRef.current) {
                fileInputRef.current.value = "";
            }
        }
    };

    // Store image in memory and update content with a placeholder
    const handleImageInsert = (file: File) => {
        const reader = new FileReader();
        reader.onload = (e) => {
            const dataUrl = e.target?.result as string;
            const imageId = `img-${Date.now()}`;
            const placeholderText = `[Image: ${file.name}]`;

            // Store the image data
            setImages(prev => [...prev, {
                id: imageId,
                name: file.name,
                dataUrl
            }]);

            // Insert a placeholder in the editor
            // The actual image data will be processed on submission
            setContent(prevContent => {
                return prevContent
                    ? `${prevContent}\n\n![${file.name}](${imageId})`
                    : `![${file.name}](${imageId})`;
            });
        };
        reader.readAsDataURL(file);
    };

    // Handle drag over event to enable drop zone
    const handleDragOver = (e: React.DragEvent) => {
        e.preventDefault();
    };

    // Handle drop event for images
    const handleDrop = async (e: React.DragEvent) => {
        e.preventDefault();

        const files = e.dataTransfer.files;
        if (files && files.length > 0) {
            const file = files[0];
            if (file.type.startsWith('image/')) {
                handleImageInsert(file);
            } else {
                setError("Only image files are accepted for upload.");
            }
        }
    };

    // Handle submit
    const handleSubmit = async () => {
        if (!title || !content) {
            setError("Please fill all the required fields.");
            return;
        }

        setIsLoading(true);
        setError("");

        try {
            // Replace image placeholders with actual base64 data before submission
            let finalContent = content;

            // Replace all image IDs with their actual data URLs
            images.forEach(img => {
                const regex = new RegExp(`\\(${img.id}\\)`, 'g');
                finalContent = finalContent.replace(regex, `(${img.dataUrl})`);
            });

            // Create the post with embedded image content
            const payload: PostPayload = {
                postTitle: title,
                postContent: finalContent,
                userId: userId
            };

            // Submit the post
            const response = await apiService.post<string>("/forum/create", payload);

            // Reset form
            setTitle("");
            setContent("");
            setImages([]);

            // Show success message or redirect
            alert("Post submitted successfully!");
            navigate("/forum"); // Navigate to forum page after successful submission
        } catch (err: any) {
            // Handle error from apiService
            const errorMessage = err.response?.data?.message || "Failed to create post. Please try again.";
            setError(errorMessage);
            console.error("Post creation error:", err);
        } finally {
            setIsLoading(false);
        }
    };

    // Custom preview options to show images properly in the preview pane
    const previewOptions = {
        components: {
            img: (props: any) => {
                // Check if this is one of our image IDs
                const imageData = images.find(img => img.id === props.src);
                if (imageData) {
                    // If it's one of our images, render using the actual data URL
                    return <img src={imageData.dataUrl} alt={props.alt || ""} />;
                }
                // Otherwise, render normally
                return <img src={props.src} alt={props.alt || ""} />;
            }
        }
    };

    return (
        <div className="container mx-auto p-4 space-y-6">
            <h1 className="text-2xl font-bold text-teal-700">Create New Post</h1>

            {userName && (
                <p className="text-gray-600">Creating as: <span className="font-medium">{userName}</span></p>
            )}

            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded">
                    {error}
                </div>
            )}

            {/* Title Input */}
            <div>
                <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
                    Post Title *
                </label>
                <input
                    id="title"
                    type="text"
                    placeholder="Enter post title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="w-full p-2 border rounded text-gray-600 focus:ring-teal-500 focus:border-teal-500"
                    required
                />
            </div>

            {/* Markdown Editor with Drop Zone */}
            <div
                onDragOver={handleDragOver}
                onDrop={handleDrop}
                className="border-2 border-transparent rounded transition-colors"
            >
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Post Content *
                </label>
                <MDEditor
                    ref={editorRef}
                    value={content}
                    onChange={(val) => setContent(val || "")}
                    height={400}
                    previewOptions={previewOptions}
                />
            </div>

            {/* Image Upload Button */}
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    Images
                </label>
                <div className="flex items-center space-x-4">
                    <input
                        type="file"
                        accept="image/*"
                        ref={fileInputRef}
                        onChange={handleFileChange}
                        className="hidden"
                        id="image-upload"
                    />
                    <button
                        onClick={() => fileInputRef.current?.click()}
                        className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
                        type="button"
                    >
                        Upload Image
                    </button>
                    <p className="text-sm text-gray-500">
                        You can also paste images or drag & drop them into the editor
                    </p>
                </div>
            </div>

            {/* Submit Button */}
            <div>
                <button
                    onClick={handleSubmit}
                    disabled={isLoading}
                    className="px-6 py-2 bg-teal-700 text-white font-medium rounded hover:bg-teal-800 disabled:bg-gray-400"
                >
                    {isLoading ? "Submitting..." : "Submit Post"}
                </button>
            </div>
        </div>
    );
}