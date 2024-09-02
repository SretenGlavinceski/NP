package Midterm1.ex17;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Category {
    String categoryName;
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryName, category.categoryName);
    }

    public String getCategoryName() {
        return categoryName;
    }
}

abstract class NewsItem {
    String title;
    Date date;
    Category category;

    public NewsItem(String title, Date date, Category category) {
        this.title = title;
        this.date = date;
        this.category = category;
    }
    abstract public String getTeaser();
}

class TextNewsItem extends NewsItem {
    String content;

    public TextNewsItem(String title, Date date, Category category, String content) {
        super(title, date, category);
        this.content = content;
    }
    @Override
    public String getTeaser() {
        Date current = new Date();
        String contentText;
        if (content.length() < 80)
            contentText = content;
        else
            contentText = content.substring(0, 80);
        return String.format("%s\n%s\n%s\n", title, (current.getTime() - date.getTime()) / (1000 * 60), contentText);
    }
}

class MediaNewsItem extends NewsItem {
    String url;
    int viewerCount;

    public MediaNewsItem(String title, Date date, Category category, String url, int viewerCount) {
        super(title, date, category);
        this.url = url;
        this.viewerCount = viewerCount;
    }
    @Override
    public String getTeaser() {
        Date current = new Date();
        return String.format("%s\n%d\n%s\n%d\n",
                title,
                (current.getTime() - date.getTime()) / (1000 * 60),
                url,
                viewerCount);
    }
}

class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String s) {
        super(s);
    }
}

class FrontPage {
    Category [] categories;
    List<NewsItem> news;

    public FrontPage(Category[] categories) {
        news = new ArrayList<>();
        this.categories = categories;
    }

    void addNewsItem(NewsItem newsItem) {
        news.add(newsItem);
    }
    List<NewsItem> listByCategory(Category category) {
        return news.stream().filter(newsItem -> newsItem.category.equals(category))
                .collect(Collectors.toList());
    }
    List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
        if (Arrays.stream(categories).noneMatch(i -> i.getCategoryName().equals(category)))
            throw new CategoryNotFoundException(String.format("Category %s was not found", category));
        return listByCategory(new Category(category));
    }

    @Override
    public String toString() {
        return news.stream().map(NewsItem::getTeaser).collect(Collectors.joining());
    }
}