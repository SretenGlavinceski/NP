package Midterm2.ex37;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}

class ComparatorFactory {
    static Comparator<Comment> generateComparator () {
        return Comparator.comparing(Comment::getAllCommentLikes).reversed();
    }
}

class Comment {
    String username;
    String commentId;
    String content;
    List<Comment> comments;
    int likes = 0;

    public Comment(String username, String commentId, String content) {
        this.username = username;
        this.commentId = commentId;
        this.content = content;
        comments = new ArrayList<>();
    }

    public void addComment (Comment comment) {
        comments.add(comment);
    }

    public Comment findComment(String replyToId) {
        if (commentId.equals(replyToId))
            return this;

        for (Comment comment : comments) {
            if (comment.getCommentId().equals(replyToId))
                return comment;
            else if (comment.findComment(replyToId) != null)
                return comment.findComment(replyToId);
        }

        return null;
    }

    public void addLike () {
        likes++;
    }

    public String getCommentId() {
        return commentId;
    }

    public int getAllCommentLikes () {
        int temp = likes;

        for (Comment comment : comments) {
            temp += comment.getAllCommentLikes();
        }

        return temp;
    }

    public String display(String indent) {
        StringBuilder sb = new StringBuilder(
                String.format("\n%sComment: %s\n%sWritten by: %s\n%sLikes: %d",
                    indent,
                    content,
                    indent,
                    username,
                    indent,
                    likes)
        );

        comments.stream()
                .sorted(ComparatorFactory.generateComparator())
                .forEach(i -> sb.append(i.display(indent + "    ")));

        return sb.toString();
    }

}

class Post {
    String username;
    String postContent;
    List<Comment> comments;

    Post(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
        comments = new ArrayList<>();
    }

    void addComment (String username, String commentId, String content, String replyToId) {
        Comment comment = new Comment(username, commentId, content);
        if (replyToId == null) {
            comments.add(comment);
            return;
        }

        comments.forEach(c -> {
            Comment temp = c.findComment(replyToId);
            if (temp != null)
                temp.addComment(comment);
        });

    }

    void likeComment (String commentId) {
        comments.forEach(c -> {
            Comment temp = c.findComment(commentId);
            if (temp != null) {
                temp.addLike();
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(
                String.format("Post: %s\nWritten by: %s\nComments:",
                        postContent,
                        username)
        );

        comments.stream()
                .sorted(ComparatorFactory.generateComparator())
                .forEach(i->sb.append(i.display("        ")));

        return sb.toString();

    }
}
