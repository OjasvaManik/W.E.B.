CREATE TABLE posts (
                       post_id UUID PRIMARY KEY,
                       post_title VARCHAR(255) NOT NULL,
                       post_content TEXT NOT NULL,
                       user_id UUID NOT NULL,
                       CONSTRAINT fk_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(user_id)
                                ON DELETE CASCADE
);


CREATE TABLE comments (
                          comment_id UUID PRIMARY KEY,
                          comment_content TEXT,
                          post_id UUID NOT NULL,
                          user_id UUID NOT NULL,
                          CONSTRAINT fk_comments_post
                              FOREIGN KEY (post_id)
                                  REFERENCES posts(post_id)
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_comments_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(user_id)
                                  ON DELETE CASCADE
);


CREATE TABLE post_votes (
                            vote_id UUID PRIMARY KEY,
                            vote_type VARCHAR(255),
                            post_id UUID NOT NULL,
                            user_id UUID NOT NULL,
                            CONSTRAINT fk_post_vote_post
                                FOREIGN KEY (post_id)
                                    REFERENCES posts(post_id)
                                    ON DELETE CASCADE,
                            CONSTRAINT fk_post_vote_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users(user_id)
                                    ON DELETE CASCADE,
                            CONSTRAINT unique_post_user_vote
                                UNIQUE (post_id, user_id)
);


CREATE TABLE comment_votes (
                               vote_id UUID PRIMARY KEY,
                               vote_type VARCHAR(255),
                               comment_id UUID NOT NULL,
                               user_id UUID NOT NULL,
                               CONSTRAINT fk_comment_vote_comment
                                   FOREIGN KEY (comment_id)
                                       REFERENCES comments(comment_id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_comment_vote_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(user_id)
                                       ON DELETE CASCADE,
                               CONSTRAINT unique_comment_user_vote
                                   UNIQUE (comment_id, user_id)
);
