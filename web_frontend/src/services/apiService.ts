// import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
//
// // Create a typed API client
// class ApiService {
//     private api: AxiosInstance;
//
//     constructor(baseURL: string) {
//         this.api = axios.create({
//             baseURL,
//             headers: {
//                 'Content-Type': 'application/json'
//             }
//         });
//
//         // Add request interceptor to include auth token
//         this.api.interceptors.request.use(
//             (config) => {
//                 const token = localStorage.getItem('jwtToken');
//                 if (token && config.headers) {
//                     config.headers['Authorization'] = `Bearer ${token}`;
//                 }
//                 return config;
//             },
//             (error) => Promise.reject(error)
//         );
//     }
//
//     // Generic GET method with type parameter
//     public async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
//         const response: AxiosResponse<T> = await this.api.get<T>(url, config);
//         return response.data;
//     }
//
//     // Generic POST method with type parameters
//     public async post<T, D = any>(url: string, data?: D, config?: AxiosRequestConfig): Promise<T> {
//         const response: AxiosResponse<T> = await this.api.post<T>(url, data, config);
//         return response.data;
//     }
//
//     // Generic PUT method with type parameters
//     public async put<T, D = any>(url: string, data?: D, config?: AxiosRequestConfig): Promise<T> {
//         const response: AxiosResponse<T> = await this.api.put<T>(url, data, config);
//         return response.data;
//     }
//
//     // Generic DELETE method with type parameter
//     public async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
//         const response: AxiosResponse<T> = await this.api.delete<T>(url, config);
//         return response.data;
//     }
// }
//
// // Create and export instance
// const apiService = new ApiService('http://localhost:8080/api/v1/web');
//
// export default apiService;

import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

// Define a generic interface for better type safety
interface ApiResponse<T> {
    data: T;
    status: number;
    statusText: string;
    headers: any;
}

class ApiService {
    private api: AxiosInstance;

    constructor(baseURL: string) {
        this.api = axios.create({
            baseURL,
            headers: {
                'Content-Type': 'application/json'
            }
        });

        this.initializeInterceptors();
    }

    private initializeInterceptors() {
        // Request interceptor for auth token
        this.api.interceptors.request.use(
            (config) => {
                const token = localStorage.getItem('jwtToken');
                if (token && config.headers) {
                    config.headers['Authorization'] = `Bearer ${token}`;
                }
                return config;
            },
            (error) => Promise.reject(error)
        );

        // Response interceptor for error handling
        this.api.interceptors.response.use(
            (response) => response,
            (error) => {
                if (error.response) {
                    console.error('API Error:', error.response.data);
                }
                return Promise.reject(error);
            }
        );
    }

    // Enhanced GET method with better typing
    public async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.get<T, ApiResponse<T>>(url, config);
        return response.data;
    }

    // Enhanced POST method with type safety
    public async post<T, D = any>(url: string, data?: D, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.post<T, ApiResponse<T>, D>(url, data, config);
        return response.data;
    }

    // New FormData-specific POST method
    public async postFormData<T>(url: string, formData: FormData, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.post<T>(url, formData, {
            ...config,
            headers: {
                ...config?.headers,
                'Content-Type': 'multipart/form-data', // Force correct content type
            },
        });
        return response.data;
    }

    // Enhanced PUT method
    public async put<T, D = any>(url: string, data?: D, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.put<T, ApiResponse<T>, D>(url, data, config);
        return response.data;
    }

    // Enhanced DELETE method
    public async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.api.delete<T, ApiResponse<T>>(url, config);
        return response.data;
    }
}

// Create and export instance with your base URL
const apiService = new ApiService('http://localhost:8080/api/v1/web');

export default apiService;