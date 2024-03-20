import java.io.IOException;

/**
 * Represents a frog's path on a pond.
 * Finds the best path for a frog to hop from one hexagon to another while avoiding obstacles.
 *
 * @author Justin Dhillon JDHILL94 251348823
 */
public class FrogPath {
    private Pond pond; // The pond where the frog hops.

    /**
     * Constructs a FrogPath object with the specified filename.
     *
     * @param filename The name of the file containing the pond layout.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public FrogPath(String filename) throws IOException {

        try {
            // Init pond
            pond = new Pond(filename);
        } catch (IOException ignored) {
            // Ignoring the IOException.
        }
    }

    /**
     * Finds the best next hexagon for the frog to hop to.
     *
     * @param currCell The current hexagon where the frog is located.
     * @return The best next hexagon for the frog to hop to, or null if no valid hexagon is found.
     */
    public Hexagon findBest(Hexagon currCell) {

        ArrayUniquePriorityQueue<Hexagon> pq = new ArrayUniquePriorityQueue<>();

        try {
            Hexagon neighbour;
            Hexagon newNeighbour;

            for (int i = 0; i < 6; i++) {
                neighbour = currCell.getNeighbour(i);
                if (isValidCell(neighbour)) {
                    pq.add(neighbour, calcPriority(neighbour));
                }
            }

            if (currCell.isLilyPadCell() || currCell.isStart()) {
                for (int i = 0; i < 6; i++) {
                    neighbour = currCell.getNeighbour(i);
                    for (int j = 0; j < 6; j++) {

                        try {
                            newNeighbour = neighbour.getNeighbour(j);
                            if (isValidCell(newNeighbour)) {
                                pq.add(newNeighbour, calcPriority(newNeighbour, currCell));
                            }
                        } catch (Exception ignored) {
                            // Ignoring any exceptions.
                        }

                    }
                }
            }
        } catch (Exception ignored) {
            // Ignoring any exceptions.
        }

        if (pq.isEmpty()) {
            return null;
        }
        return pq.removeMin();
    }

    /**
     * Checks if a cell is a valid cell for the frog to hop to.
     *
     * @param cell The cell to check.
     * @return True if the cell is valid, false otherwise.
     */
    private boolean isValidCell(Hexagon cell) {

        if (cell == null) return false; // Check if cell is null.
        if (cell == pond.getStart()) return false; // Avoid starting cell.

        if (cell.isMarked() || cell.isMudCell() || cell.isAlligator())
            return false; // Avoid marked, mud, and alligator cells.

        if (cell.isReedsCell()) return true; // Reeds cell is valid.

        for (int i = 0; i < 6; i++) {
            try {
                if (cell.getNeighbour(i).isAlligator()) return false; // Avoid alligator neighbors.
            } catch (Exception ignored) {
                // Ignoring any exceptions.
            }
        }

        return true;

    }

    /**
     * Calculates the priority of a cell based on certain criteria.
     *
     * @param cell The cell for which to calculate priority.
     * @return The priority of the cell.
     */
    private double calcPriority(Hexagon cell) {
        double priority = 0.0;

        if (cell instanceof FoodHexagon) {
            switch (((FoodHexagon) cell).getNumFlies()) {
                case 3:
                    priority = 0;
                    break;
                case 2:
                    priority = 1;
                    break;
                case 1:
                    priority = 2;
                    break;
                default:
                    priority += 0;
            }
        }

        if (cell.isEnd()) priority = 3;
        else if (cell.isLilyPadCell()) priority = 4;
        else if (cell.isReedsCell()) priority = 5;
        else if (cell.isWaterCell()) priority = 6;

        if (cell.isReedsCell() && checkNearAlligators(cell)) priority = 10;

        return priority;
    }

    /**
     * Calculates the priority of a cell based on certain criteria and the current cell.
     *
     * @param cell     The cell for which to calculate priority.
     * @param currCell The current cell.
     * @return The priority of the cell.
     */
    private double calcPriority(Hexagon cell, Hexagon currCell) {
        double prio = calcPriority(cell); // Reusing the existing logic

        // Additional priority calculation...

        if (isTwoAwayStraight(cell, currCell)) {
            prio += 0.5;
        } else {
            prio += 1.0;
        }

        return prio;
    }

    /**
     * Checks if there are alligators near the given cell.
     *
     * @param cell The cell to check.
     * @return True if there are alligators nearby, false otherwise.
     */
    private boolean checkNearAlligators(Hexagon cell) {
        for (int i = 0; i < 6; i++) {
            try {
                if (cell.getNeighbour(i).isAlligator()) return true; // Check for alligator neighbors.
            } catch (Exception ignored) {
                // Ignoring any exceptions.
            }
        }
        return false;
    }

    /**
     * Finds the path for the frog to hop.
     *
     * @return The path of the frog.
     */
    public String findPath() {
        ArrayStack<Hexagon> s = new ArrayStack(); // Stack to store the path.
        s.push(pond.getStart()); // Push the starting cell.
        int filesEaton = 0; // Count of flies eaten.

        StringBuilder output = new StringBuilder(); // Output string.

        Hexagon curr;
        while (!s.isEmpty()) {

            curr = s.peek();

            output.append(s.peek().getID()).append(" "); // Append the current cell's ID to the output.
            if (curr.isEnd()) break; // Break loop if the end cell is reached.

            if (curr instanceof FoodHexagon) {
                filesEaton = filesEaton + ((FoodHexagon) curr).getNumFlies(); // Increase flies eaten count.
                ((FoodHexagon) curr).removeFlies(); // Remove flies from the food hexagon.
            }
            Hexagon next = findBest(curr); // Find the next best hexagon to hop to.
            if (next == null) {
                s.pop(); // Pop current cell if no valid next cell is found.
                curr.markOutStack(); // Mark current cell as out of stack.
            } else {
                s.push(next); // Push next cell to the stack.
                next.markInStack(); // Mark next cell as in stack.
            }
        }

        if (s.isEmpty()) return "No solution"; // If stack is empty, no solution.
        output.append("ate ").append(filesEaton).append(" flies"); // Append flies eaten


        return output.toString();
    }

    /**
     * Checks if the given cell is two cells away in a diagonal line.
     *
     * @param cell The cell to check
     * @return True if the cell is two away in a diagonal line, false otherwise
     */
    private boolean isTwoAwayStraight(Hexagon cell, Hexagon currCell) {
        int count = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (cell.getNeighbour(i) == currCell.getNeighbour(j)) count++;
            }
        }

        return count == 1;
    }


}
