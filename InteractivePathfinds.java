package interactivepathfinds;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InteractivePathfinds extends JFrame {
    private final Random r = new Random();
    private final int cols = 100;
    private final int rows = 100;
    private Node start;
    private Node end;
    private Node[][] grid;
    private Node[] temp;
    private Node current;
    private ArrayList<Node> openSet;// list of nodes needed to be evaluated
    private PriorityQueue<Node> openSetQueue;// same as openSet but automatically sorted and controlled
    private ArrayList<Node> closedSet;//list of nodes that is done being evaluate
    private final int WIDTH = 1000;
    private final int HEIGHT = 1000;
    private final double dense = 0.5;
    private final int millis = 0;
    private final String[] algorithms = {"A*", "Dijkstra"};
    private final static int algorithm = 2;// 0 = astar, 1 = dijkstra, 2 = idastar
    private final static int distCal = 0;// 0 = euclidean, 1 = manhattan, 2 = octile, 3 = chevbyshev
    private final int edgeCost = 10;
    private final int diagonalCost = 14;
    private final int allowDiagonal = 1;
    private final JLabel statusbar;
    private long timeBegin;
    private long timePass;
    private int checks = 0;
    private int loops = 0;

    public void astar() {
        openSet = new ArrayList<>();
        openSet.add(start);
        closedSet = new ArrayList<>();
        while (!openSet.isEmpty()) {
            int pathlength = 0;
            checks++;
            int preferedPathIndex = 0;
            temp = openSet.toArray(new Node[openSet.size()]);

            for (int i = 0; i < temp.length; i++) {
                if (temp[i].getF() < temp[preferedPathIndex].getF()) {
                    preferedPathIndex = i;
                }
                if (temp[i].getF() == temp[preferedPathIndex].getF()) {
                    if (temp[i].getTotalG() > temp[preferedPathIndex].getTotalG()) {
                        preferedPathIndex = i;
                    }
                }
            }

            current = openSet.remove(preferedPathIndex);

            Node path = current;
            while (path.getPrevious() != null && path.getPrevious() != start) {
                if (path.getPrevious() != end) {
                    path.getPrevious().setType(6);
                }
                path = path.getPrevious();
                pathlength++;
            }

            if (current == end) {
                System.out.println("Path found!");
                grid[end.getX()][end.getY()].setType(1);
                revalidate();
                repaint();
                return;
            } else {
                revalidate();
                repaint();
                path = current;
                while (path.getPrevious() != null && path.getPrevious() != start) {
                    path.getPrevious().setType(5);
                    path = path.getPrevious();
                }
                grid[start.getX()][start.getY()].setType(0);
            }

            closedSet.add(current);
            current.setType(5);
            grid[current.getX()][current.getY()] = current;

            Node[] neighbors = current.getNeighbors().toArray(new Node[current.getNeighbors().size()]);

            for (Node neighbor : neighbors) {
                if (!closedSet.contains(neighbor)) {
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    } else {
                        continue;
                    }

                    neighbor.setG(distCalculation(neighbor, current, distCal));
                    if (neighbor.previous != null) {
                        neighbor.setTotalG(neighbor.getG() + neighbor.previous.totalg);
                    } else {
                        neighbor.setTotalG(neighbor.getG());
                    }
                    neighbor.setH(distCalculation(neighbor, end, distCal));
                    neighbor.setF(neighbor.getTotalG() + neighbor.getH());
                    neighbor.setPrevious(current);
                    if (neighbor != end) {
                        neighbor.setType(4);
                    }
                    grid[neighbor.getX()][neighbor.getY()] = neighbor;
                    loops++;
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(InteractivePathfinds.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            timePass = System.currentTimeMillis() - timeBegin;
            statusbar.setText(String.format("checks: %d  loops: %d  pathlength: %d  timer ms: %d", loops, checks, pathlength, timePass));
        }

        System.out.println("No solution.");
    }

    public void dijkstra() {
        openSetQueue = new PriorityQueue<>();
        closedSet = new ArrayList<>();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                grid[x][y].setF(Integer.MAX_VALUE);
            }
        }
        start.setF(0);
        openSetQueue.add(start);
        while (!openSetQueue.isEmpty()) {
            int pathlength = 0;
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                Logger.getLogger(InteractivePathfinds.class.getName()).log(Level.SEVERE, null, ex);
            }
            checks++;
            current = openSetQueue.poll();
            Node path = current;
            while (path.getPrevious() != null && path.getPrevious() != start) {
                if (path.getPrevious() != end) {
                    path.getPrevious().setType(6);
                }
                path = path.getPrevious();
                pathlength++;
            }

            if (current == end) {
                System.out.println("Path found!");
                grid[end.getX()][end.getY()].setType(1);
                revalidate();
                repaint();
                return;
            } else {
                revalidate();
                repaint();
                path = current;
                while (path.getPrevious() != null && path.getPrevious() != start) {
                    path.getPrevious().setType(5);
                    path = path.getPrevious();
                }
                grid[start.getX()][start.getY()].setType(0);
            }

            closedSet.add(current);
            current.setType(5);
            grid[current.getX()][current.getY()] = current;

            Node[] neighbors = current.getNeighbors().toArray(new Node[current.getNeighbors().size()]);

            for (Node neighbor : neighbors) {
                if (!closedSet.contains(neighbor)) {
                    if (!openSetQueue.contains(neighbor)) {
                        openSetQueue.add(neighbor);
                    } else {
                        continue;
                    }
                    int edgeDistance = distCalculation(neighbor, current, distCal);
                    int newDistance = current.getF() + edgeDistance;
                    if (neighbor.getF() > newDistance) {
                        neighbor.setF(newDistance);
                    }
                    neighbor.setType(4);
                    neighbor.setPrevious(current);
                    grid[neighbor.getX()][neighbor.getY()] = neighbor;
                    loops++;
                }
            }

            timePass = System.currentTimeMillis() - timeBegin;
            statusbar.setText(String.format("loops: %d  checks: %d  pathlength: %d  timer ms: %d", loops, checks, pathlength, timePass));
        }

        System.out.println("No solution.");
    }

    public void idastar() {
        closedSet = new ArrayList<>();
        int bound = distCalculation(start, end, distCal);
        Stack path = new Stack();
        path.push(start);
        while (true) {
            loops++;
            int t = search(path, 0, bound);
            if (t == 0) {
                System.out.println("Path found!");
                while (!path.isEmpty()) {
                    Node node = (Node) path.pop();
                    grid[node.getX()][node.getY()].setType(6);
                }
                start.setType(0);
                end.setType(1);
                revalidate();
                repaint();
                return;
            }
            if (t == Integer.MAX_VALUE) {
                System.out.println("No solution.");
                return;
            }
            bound = t;
        }
    }

    public int search(Stack path, int g, int bound) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(InteractivePathfinds.class.getName()).log(Level.SEVERE, null, ex);
        }
        Node node = (Node) path.peek();
        int f = distCalculation(node, end, distCal) + g;
        if (f > bound) {
            bound++;
        } else {
            bound = f;
        }
        if (node == end) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        Node[] neighbors = node.getNeighbors().toArray(new Node[node.getNeighbors().size()]);

        for (Node neighbor : neighbors) {
            if (!path.contains(neighbor) && !closedSet.contains(neighbor)) {
                checks++;
                path.push(neighbor);
                neighbor.setType(5);
                grid[neighbor.getX()][neighbor.getY()] = neighbor;
                revalidate();
                repaint();
                int t = search(path, g + distCalculation(node, neighbor, distCal), bound);
                if (t == 0) {
                    return 0;
                }
                if (t < min) {
                    min = t;
                }
                closedSet.add(neighbor);
                path.pop();
            }
        }
        timePass = System.currentTimeMillis() - timeBegin;
        statusbar.setText(String.format("loops: %d  checks: %d  timer ms: %d", loops, checks, timePass));
        return min;
    }

    public static void main(String[] args) {
        new InteractivePathfinds(algorithm);
    }

    public InteractivePathfinds(int alg) {
        setTitle("Pathfinds");
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        statusbar = new JLabel();
        add(statusbar, BorderLayout.SOUTH);

        generate(alg);
    }

    public void generate(int alg) {
        grid = new Node[cols][rows];
        double num;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                num = r.nextDouble();
                if (num < dense) {
                    grid[x][y] = new Node(2, x, y);
                } else {
                    grid[x][y] = new Node(3, x, y);
                }

            }
        }

//        start = new Node(0, 0, 0);
//        grid[0][0] = start;
//        end = new Node(1, rows - 1, cols - 1);
//        grid[rows - 1][cols - 1] = end;

        double startx = r.nextDouble();
        double starty = r.nextDouble();
        start = new Node(0, (int) Math.floor(startx * cols), (int) Math.floor(starty * rows));
        grid[(int) Math.floor(startx * cols)][(int) Math.floor(starty * rows)] = start;
        double endx = r.nextDouble();
        double endy = r.nextDouble();
        while (endx == startx && endy == starty) {
            endx = r.nextDouble();
            endy = r.nextDouble();
        }
        end = new Node(1, (int) Math.floor(endx * cols), (int) Math.floor(endy * rows));
        grid[(int) Math.floor(endx * cols)][(int) Math.floor(endy * rows)] = end;

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if (allowDiagonal == 0) {
                    grid[x][y].addNeighborsEdges();
                } else {
                    grid[x][y].addNeighbors();
                }
            }
        }

        timeBegin = System.currentTimeMillis();

        if (alg == 0) {
            astar();
        } else if (alg == 1) {
            dijkstra();
        } else if (alg == 2) {
            idastar();
        }
    }

    @Override
    public void paint(Graphics g) {
        int w = HEIGHT / rows;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                switch (grid[x][y].getType()) {
                    case 0 -> g.setColor(Color.YELLOW);
                    case 1 -> g.setColor(Color.ORANGE);
                    case 2 -> g.setColor(Color.BLACK);
                    case 3 -> g.setColor(Color.WHITE);
                    case 4 -> g.setColor(Color.GREEN);
                    case 5 -> g.setColor(Color.RED);
                    case 6 -> g.setColor(Color.BLUE);
                    default -> {
                    }
                }
                g.fillRect(x * w, y * w, x * w + w, y * w + w);
            }
        }
    }

    public int distCalculation(Node a, Node b, int value) {
        return switch (value) {
            case 0 -> distEuclidean(a, b);
            case 1 -> distManhattan(a, b);
            case 2 -> distOctile(a, b);
            case 3 -> distChebyshev(a, b);
            default -> distEuclidean(a, b);
        };
    }

    public int distEuclidean(Node a, Node b) {
        int xdif = a.getX() - b.getX();
        int ydif = a.getY() - b.getY();
        return (int) Math.ceil(Math.sqrt((double) (xdif * xdif) + (double) (ydif * ydif)) * edgeCost);
    }

    public int distManhattan(Node a, Node b) {
        return (int) edgeCost * (Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()));
    }

    public int distOctile(Node a, Node b) {
        int xdif = a.getX() - b.getX();
        int ydif = a.getY() - b.getY();
        return edgeCost * (xdif + ydif) + (edgeCost - 2 * edgeCost) * Math.min(xdif, ydif);
    }

    public int distChebyshev(Node a, Node b) {
        int xdif = a.getX() - b.getX();
        int ydif = a.getY() - b.getY();
        return (int) Math.ceil(edgeCost * (xdif + ydif) + (diagonalCost - 2 * Math.sqrt(edgeCost)) * Math.min(xdif, ydif));
    }

    class Node implements Comparable<Node> {

        private final int x;
        private final int y;
        private int f;//finalcost f = g + h
        private int g;//cost g(n) from start
        private int totalg;
        private int h;//cost h(n) from end
        private int type;//0 - start, 1 - end, 2 - wall, 3 - empty, 4 - open, 5 - closed, 6 - solution
        private final ArrayList<Node> neighbors = new ArrayList<>();
        private Node previous = null;

        public Node(int type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }

        public void addNeighbors() {
            if (x < cols - 1 && grid[x + 1][y].getType() != 2) {
                neighbors.add(grid[x + 1][y]);
            }
            if (x > 0 && grid[x - 1][y].getType() != 2) {
                neighbors.add(grid[x - 1][y]);
            }
            if (y < rows - 1 && grid[x][y + 1].getType() != 2) {
                neighbors.add(grid[x][y + 1]);
            }
            if (y > 0 && grid[x][y - 1].getType() != 2) {
                neighbors.add(grid[x][y - 1]);
            }
            if (x > 0 && y > 0 && grid[x - 1][y - 1].getType() != 2) {
                neighbors.add(grid[x - 1][y - 1]);
            }
            if (x < cols - 1 && y > 0 && grid[x + 1][y - 1].getType() != 2) {
                neighbors.add(grid[x + 1][y - 1]);
            }
            if (x < cols - 1 && y < rows - 1 && grid[x + 1][y + 1].getType() != 2) {
                neighbors.add(grid[x + 1][y + 1]);
            }
            if (x > 0 && y < rows - 1 && grid[x - 1][y + 1].getType() != 2) {
                neighbors.add(grid[x - 1][y + 1]);
            }
        }

        public void addNeighborsEdges() {
            if (x < cols - 1 && grid[x + 1][y].getType() != 2) {
                neighbors.add(grid[x + 1][y]);
            }
            if (x > 0 && grid[x - 1][y].getType() != 2) {
                neighbors.add(grid[x - 1][y]);
            }
            if (y < rows - 1 && grid[x][y + 1].getType() != 2) {
                neighbors.add(grid[x][y + 1]);
            }
            if (y > 0 && grid[x][y - 1].getType() != 2) {
                neighbors.add(grid[x][y - 1]);
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getF() {
            return f;
        }

        public int getG() {
            return g;
        }

        public int getTotalG() {
            return totalg;
        }

        public int getH() {
            return h;
        }

        public int getType() {
            return type;
        }

        public ArrayList<Node> getNeighbors() {
            return neighbors;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setF(int f) {
            this.f = f;
        }

        public void setG(int g) {
            this.g = g;
        }

        public void setTotalG(int totalg) {
            this.totalg = totalg;
        }

        public void setH(int h) {
            this.h = h;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }

        @Override
        public int compareTo(Node node) {
            return Integer.compare(getF(), node.getF());
        }
    }
}