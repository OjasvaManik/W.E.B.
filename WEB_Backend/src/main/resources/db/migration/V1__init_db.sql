CREATE TABLE IF NOT EXISTS users (
                                     user_id UUID PRIMARY KEY,
                                     first_name VARCHAR(50) NOT NULL,
                                    last_name VARCHAR(50) NOT NULL,
                                    email VARCHAR(255) NOT NULL UNIQUE,
                                    password VARCHAR(255) NOT NULL,
                                    role VARCHAR(20) NOT NULL,
                                    user_name VARCHAR(30) NOT NULL UNIQUE,
                                    is_banned BOOLEAN NOT NULL DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS user_profile_image (
                                                  profile_image_id UUID PRIMARY KEY,
                                                  profile_image_name VARCHAR(255) NOT NULL,
    profile_image_type VARCHAR(255) NOT NULL CHECK (
                                                       profile_image_type ~ '^(image/(png|jpeg|jpg|webp|svg))$'
                                                   ),
    profile_image_file_path VARCHAR(512) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_user_profile_image_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS category (
                                        category_id UUID PRIMARY KEY NOT NULL,
                                        category_name VARCHAR(50) NOT NULL,
    category_title VARCHAR(150) NOT NULL
    );

CREATE TABLE IF NOT EXISTS articles (
                                        article_id UUID PRIMARY KEY,
                                        article_title VARCHAR(500) NOT NULL,
    article_content TEXT NOT NULL,
    article_source VARCHAR(1000) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_article_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS article_category (
                                                article_id UUID NOT NULL,
                                                category_id UUID NOT NULL,
                                                PRIMARY KEY (article_id, category_id),
    CONSTRAINT fk_article_category_article FOREIGN KEY (article_id) REFERENCES articles(article_id),
    CONSTRAINT fk_article_category_category FOREIGN KEY (category_id) REFERENCES category(category_id)
    );

CREATE TABLE IF NOT EXISTS article_status (
                                              article_id UUID PRIMARY KEY,
                                              status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_status_article FOREIGN KEY (article_id) REFERENCES articles(article_id)
    );


CREATE TABLE article_images (
                                article_image_id UUID PRIMARY KEY,
                                article_image_name VARCHAR(255) NOT NULL,
                                article_image_type VARCHAR(255) NOT NULL CHECK (
                                    article_image_type ~ '^image/(png|jpeg|jpg|webp|svg)$'
),
                                article_image_file_path VARCHAR(512) NOT NULL,
                                article_id UUID NOT NULL UNIQUE,
                                CONSTRAINT fk_article_image_article FOREIGN KEY (article_id) REFERENCES articles(article_id)
);