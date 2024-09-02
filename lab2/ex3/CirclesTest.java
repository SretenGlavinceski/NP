package lab2.ex3;


import java.util.ArrayList;
import java.util.Scanner;

enum TYPE {
    POINT,
    CIRCLE
}

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

interface Movable {
    void moveUp(int max_y) throws ObjectCanNotBeMovedException;
    void moveLeft() throws ObjectCanNotBeMovedException;
    void moveRight(int max_x) throws ObjectCanNotBeMovedException;
    void moveDown() throws ObjectCanNotBeMovedException;
    int getCurrentXPosition();
    int getCurrentYPosition();
    TYPE getType ();
    int getxSpeed() ;

    int getySpeed() ;
    int getRadius();

}

class MovablePoint implements Movable {
    int x;
    int y;
    int xSpeed;
    int ySpeed;

    public MovablePoint(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    @Override
    public void moveUp(int max_y) throws ObjectCanNotBeMovedException {
        if (y + ySpeed > max_y)
            throw new ObjectCanNotBeMovedException();
        y = y + ySpeed;
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        if (x - xSpeed < 0)
            throw new ObjectCanNotBeMovedException();
        x = x - xSpeed;
    }

    @Override
    public void moveRight(int max_X) throws ObjectCanNotBeMovedException {
        if (x + xSpeed > max_X)
            throw new ObjectCanNotBeMovedException();
        x = x + xSpeed;
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        if (y - ySpeed < 0)
            throw new ObjectCanNotBeMovedException();
        y = y - ySpeed;
    }

    @Override
    public int getCurrentXPosition() {
        return x;
    }

    @Override
    public int getCurrentYPosition() {
        return y;
    }
    @Override
    public TYPE getType() {
        return TYPE.POINT;
    }

    @Override
    public String toString() {
        return String.format("Movable point with coordinates (%d,%d)", x, y);
    }

    public int getxSpeed() {
        return xSpeed;
    }

    public int getySpeed() {
        return ySpeed;
    }

    @Override
    public int getRadius() {
        return -1;
    }
}

class MovableCircle implements Movable {
    int radius;
    MovablePoint center;

    public MovableCircle(int radius, MovablePoint center) {
        this.radius = radius;
        this.center = center;
    }

    @Override
    public void moveUp(int max_y) throws ObjectCanNotBeMovedException {
        center.moveUp(max_y);
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        center.moveLeft();
    }

    @Override
    public void moveRight(int max_x) throws ObjectCanNotBeMovedException {
        center.moveRight(max_x);
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        center.moveDown();
    }

    @Override
    public int getCurrentXPosition() {
        return center.getCurrentXPosition();
    }

    @Override
    public int getCurrentYPosition() {
        return center.getCurrentYPosition();
    }

    @Override
    public TYPE getType() {
        return TYPE.CIRCLE;
    }

    //"Movable circle with center coordinates (" +
    //                getCurrentXPosition() + "," + getCurrentYPosition() + ") and radius " + radius;
    @Override
    public String toString() {
        return String.format("Movable circle with center coordinates (%d,%d) and radius %d",
                getCurrentXPosition(),
                getCurrentYPosition(),
                radius);
    }

    public int getxSpeed() {
        return center.xSpeed;
    }

    public int getySpeed() {
        return center.ySpeed;
    }

    public int getRadius() {
        return radius;
    }
}


class MovablesCollection {
    ArrayList<Movable> movables;
    static int MAX_X;
    static int MAX_Y;

    MovablesCollection(int x_MAX, int y_MAX) {
        movables = new ArrayList<>();
        setxMax(x_MAX);
        setyMax(y_MAX);
    }

    public static void setxMax(int maxX) {
        MAX_X = maxX;
    }

    public static void setyMax(int maxY) {
        MAX_Y = maxY;
    }

    public void addMovableObject(Movable m) throws MovableObjectNotFittableException {
        if (m.getType().equals(TYPE.CIRCLE)) {
            if (m.getCurrentXPosition() + m.getRadius() > MAX_X ||
                    m.getCurrentXPosition() - m.getRadius() < 0 ||
                    m.getCurrentYPosition() + m.getRadius() > MAX_Y ||
                    m.getCurrentYPosition() - m.getRadius() < 0)
                throw new MovableObjectNotFittableException(
                        m.toString().replace("coordinates ", "")
                );
        }

        if (m.getCurrentXPosition() > MAX_X || m.getCurrentYPosition() > MAX_Y) {
            throw new MovableObjectNotFittableException(m.toString());
        }
        movables.add(m);
    }

    public void tryMovingObject (Movable m, DIRECTION direction) throws ObjectCanNotBeMovedException {
        if (direction.equals(DIRECTION.UP))
            m.moveUp(MAX_Y);
        else if (direction.equals(DIRECTION.DOWN))
            m.moveDown();
        else if (direction.equals(DIRECTION.LEFT))
            m.moveLeft();
        else if (direction.equals(DIRECTION.RIGHT))
            m.moveRight(MAX_X);
        else
            throw new RuntimeException();
    }

    private String showMessageException (Movable movable, DIRECTION direction) {
        if (direction.equals(DIRECTION.UP))
            return String.format("Point (%d,%d) is out of bounds",
                    movable.getCurrentXPosition(),
                    movable.getCurrentYPosition() + movable.getySpeed());
        else if (direction.equals(DIRECTION.DOWN))
            return String.format("Point (%d,%d) is out of bounds",
                    movable.getCurrentXPosition(),
                    movable.getCurrentYPosition() - movable.getySpeed());
        else if (direction.equals(DIRECTION.RIGHT))
            return String.format("Point (%d,%d) is out of bounds",
                    movable.getCurrentXPosition() + movable.getxSpeed(),
                    movable.getCurrentYPosition());
        else
            return String.format("Point (%d,%d) is out of bounds",
                    movable.getCurrentXPosition() - movable.getxSpeed(),
                    movable.getCurrentYPosition());
    }

    public void moveObjectsFromTypeWithDirection (TYPE type, DIRECTION direction) {
        movables.stream().filter(i -> i.getType().equals(type))
                .forEach(object -> {
                    try {
                        tryMovingObject(object, direction);
                    } catch (ObjectCanNotBeMovedException e) {
                        System.out.println(showMessageException(object, direction));
                    }
                });
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Collection of movable objects with size ").append(movables.size()).append(":\n");
        movables.forEach(movable -> stringBuilder.append(movable).append("\n"));
        return stringBuilder.toString();
    }
}

class MovableObjectNotFittableException extends Exception {
    public MovableObjectNotFittableException(String s) {
        super(s + " can not be fitted into the collection");
    }
}
class ObjectCanNotBeMovedException extends Exception {
    public ObjectCanNotBeMovedException() {
    }
}

public class CirclesTest {

    public static void main(String[] args) {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);

            if (Integer.parseInt(parts[0]) == 0) { //point
                try {
                    collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
                } catch (MovableObjectNotFittableException e) {
                    System.out.println(e.getMessage());
                }
            } else { //circle
                int radius = Integer.parseInt(parts[5]);
                try {
                    collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
                } catch (MovableObjectNotFittableException e) {
                    System.out.println(e.getMessage());
                }
            }

        }
        System.out.println(collection.toString());

        System.out.println("MOVE POINTS TO THE LEFT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES DOWN");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);
        System.out.println(collection.toString());

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setxMax(90);
        MovablesCollection.setyMax(90);

        System.out.println("MOVE POINTS TO THE RIGHT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES UP");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);
        System.out.println(collection.toString());
    }
}
