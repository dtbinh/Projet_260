package MainPackage;

import java.awt.Color;

public class CAImageBuffer extends ImageBuffer {

    public CAImageBuffer(int width, int height) {
        super(width, height);
        this.cls(0xFF, 0xFF, 0xFF);
    }

    /**
     * update image according to value in array[][] (2D)
     * @param cells
     */
    public void update(boolean[][] cells) {
        int[][][] cellList = new int[cells.length][cells[0].length][3];
        for (int y = 0; y != cells[0].length; y++) {
            for (int x = 0; x != cells.length; x++) {
                int color;
                if (cells[x][y] == true) {
                    color = 0;
                } else {
                    color = 255;
                }
                for (int i = 0; i != 3; i++) {
                    cellList[x][y][i] = color;
                }
            }
        }
        this.update(cellList);
    }

    /**
     * update image according to real value in array[][][] (2D+colors). Color values are between 0 and 255.
     * e.g. RGB colors of pixel at coordinate (10,10) can be accessed as:
     * 	r = cells[x][y][0]
     *  g = cells[x][y][1]
     *  b = cells[x][y][2]
     * @param cells
     */
    public void update(int[][][] cells) {
        if (cells.length != this.getWidth() || cells[0].length != this.getHeight()) {
            System.err.println("array size does not match with image size.");
            System.exit(-1);
        }

        for (int y = 0; y != cells[0].length; y++) {
            for (int x = 0; x != cells.length; x++) {
                this.setPixel(x, y, cells[x][y][0], cells[x][y][1], cells[x][y][2]);
            }
        }

    }

    /**
     * update first line of image to value in array[] (1D). All image is moved one line downwards prior to update.
     * @param cells
     */
    public void update(boolean[] cells) {
        int[][] cellList = new int[cells.length][3];
        for (int x = 0; x != cells.length; x++) {
            int color;
            if (cells[x] == true) {
                color = 0;
            } else {
                color = 255;
            }
            for (int i = 0; i != 3; i++) {
                cellList[x][i] = color;
            }
        }
        this.update(cellList);
    }

    /**
     * update first line of image to value in array[][] (1D+colors). All image is moved one line downwards prior to update.
     * e.g. 
     * 	r = cells[x][0]
     *  g = cells[x][1]
     *  b = cells[x][2]
     * @param cells
     */
    public void update(int[][] cells) {
        if (cells.length != this.getWidth()) {
            System.err.println("array length does not match with image length.");
            System.exit(-1);
        }

        for (int y = 0; y != this.getHeight() - 1; y++) {
            for (int x = 0; x != this.getWidth(); x++) {
                this.setPixel(x, y, this.getPixel(x, y + 1));
            }
        }


        for (int x = 0; x != cells.length; x++) {
            this.setPixel(x, this.getHeight() - 1, cells[x][0], cells[x][1], cells[x][2]);
        }
    }

    /**
     * update forest.
     * 	cells[x][y] == 0 : no tree
     *  cells[x][y] == 1 : tree
     *  cells[x][y] == 2 : burning tree
     * @param cells
     */
    public void updateForest(int[][] cells) {
        if (cells.length != this.getWidth()) {
            System.err.println("array length does not match with image length.");
            System.exit(-1);
        }

        for (int y = 0; y != cells[0].length; y++) {
            for (int x = 0; x != cells.length; x++) {
                switch (cells[x][y]) {
                    case Case.VIDE:
                        this.setPixel(x, y, 255, 255, 255);
                        break;
                    case Case.ARBRE:
                        this.setPixel(x, y, 0, 255, 0);
                        break;
                    case Case.FEU:
                        this.setPixel(x, y, 255, 0, 0);
                        break;
                    default:
                        this.setPixel(x, y, 255, 255, 255);
                }
            }
        }
    }
    
}
