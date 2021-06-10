import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;

//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  Cell(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }

  WorldImage cellToImage() {
    return new RectangleImage(20,20, "solid", this.color);
  }

}

class Util {
  Color getRandomColor() {
    Random rand = new Random();
    int x = rand.nextInt(6);
    Color one = new Color(255, 0, 0);
    Color two = new Color(0, 255, 0);
    Color three = new Color(0, 0, 255);
    Color four = new Color(125, 125, 0);
    Color five = new Color(0, 125, 125);
    Color six = new Color(125, 0, 125);

    if (x == 1) {
      return one;
    }
    if (x == 2) {
      return two;
    }
    if (x == 3) {
      return four;
    }
    if (x == 4) {
      return five;
    }
    if (x == 5) {
      return six;
    }
    else {
      return three;
    }
  }

  public ArrayList<ArrayList<Cell>> makeRandomBoard(int size) {
    ArrayList<ArrayList<Cell>> list = new ArrayList<ArrayList<Cell>>();
    Util util = new Util();
    for (int i = 0; i < size; i++) {
      ArrayList<Cell> list2 = new ArrayList<Cell>();
      for (int j = 0; j < size; j++) {
        list2.add(new Cell(i, j, util.getRandomColor()));
      }
      list.add(list2);
    }
    return list;
  }

  void connect(FloodItWorld f) {
    for (int i = 0; i < f.size; i++) {
      for (int j = 0; j < f.size; j++) {
        if (j == 0) {
          f.board.get(i).get(j).top = null;
        }
        else {
          f.board.get(i).get(j).top = f.board.get(i).get(j - 1);
        }
        if (i == 0) {
          f.board.get(i).get(j).left = null;
        }
        else {
          f.board.get(i).get(j).left = f.board.get(i - 1).get(j);
        }
        if (j == f.size - 1) {
          f.board.get(i).get(j).bottom = null;
        }
        else {
          f.board.get(i).get(j).bottom = f.board.get(i).get(j + 1);
        }
        if (i == f.size - 1) {
          f.board.get(i).get(j).right = null;
        }
        else {
          f.board.get(i).get(j).right = f.board.get(i + 1).get(j);
        }

      }
    }
  }

  public Color getCellColor(ArrayList<ArrayList<Cell>> board, Posn loc) {
    return board.get((loc.x - 150)/20).get((loc.y - 150)/20).color;
  }

}

class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;
  int size;
  int turnsTaken;
  int totalTurns;
  int time;
  int count = 0;

  FloodItWorld(int size, int totalTurns) {
    Util util = new Util();
    this.board = util.makeRandomBoard(size);
    this.size = size;
    this.totalTurns = totalTurns;
    util.connect(this);
    board.get(0).get(0).flooded = true;
    this.time = 0;
  }

  public WorldScene makeScene() {
    WorldScene w = new WorldScene(500, 500);
    for (ArrayList<Cell> list : board) {
      for (Cell c: list) {
        w.placeImageXY(c.cellToImage(), c.x * 20 + 150, c.y * 20 + 150);
      }
    }
    w.placeImageXY(new TextImage(turnsTaken + " / " + totalTurns, new Color(0, 0, 0)), 200, 120);
    w.placeImageXY(new TextImage(time + " s", new Color(0, 0, 0)), 250, 120);
    return w;
  }

  public void onKeyEvent(String ke) {
    Util util = new Util();
    if (ke.equals("r")) {
      this.board = util.makeRandomBoard(size);
      util.connect(this);
      this.board.get(0).get(0).flooded = true;
      this.turnsTaken = 0;
      this.time = 0;
    }
  }

  public void onTick() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) { 
        if (board.get(i).get(j).flooded) {
          board.get(i).get(j).color = board.get(0).get(0).color;
        }
        else {
          //neighbor flooded && same color
          if (board.get(i).get(j).left != null) {
            if (board.get(i).get(j).left.flooded == true && board.get(i).get(j).color.equals(board.get(0).get(0).color)) {
              board.get(i).get(j).flooded = true;
            }
          }
          if (board.get(i).get(j).top != null) {
            if (board.get(i).get(j).top.flooded == true && board.get(i).get(j).color.equals(board.get(0).get(0).color)) {
              board.get(i).get(j).flooded = true;
            }
          }
          if (board.get(i).get(j).bottom != null) {
            if (board.get(i).get(j).bottom.flooded == true && board.get(i).get(j).color.equals(board.get(0).get(0).color)) {
              board.get(i).get(j).flooded = true;
            }
          }
          if (this.board.get(i).get(j).right != null) {
            if (this.board.get(i).get(j).right.flooded == true && board.get(i).get(j).color.equals(board.get(0).get(0).color)) {
              board.get(i).get(j).flooded = true;
            }
          }
        }
      }
    }
    count++;
    if (count == 100) {
      time++;
      count = 0;
    }
    

  }

  public void onMouseClicked(Posn loc) {
    Util util = new Util();
    Color col = util.getCellColor(board, loc);
    board.get(0).get(0).color = col;
    turnsTaken++;
    if (turnsTaken > totalTurns) {
       this.endOfWorld("Game over");
    }
  }
}

class ExamplesFloodIt {
  //CLICK ON BOTTOM RIGHT OF SQUARE FOR CORRECT COLOR CHANGE
  void testFlood(Tester t) {
    //FloodItWorld(int size, int turns)
    FloodItWorld w = new FloodItWorld(10, 25);
    w.bigBang(500, 500, .01);
    
    //tests util.connect()
    t.checkExpect(w.board.get(0).get(0).right, w.board.get(0).get(1));
    t.checkExpect(w.board.get(0).get(0).flooded, true);
    t.checkExpect(w.board.get(0).get(0).color, new Color(255,0,0));
    t.checkExpect(w.board.get(0).get(0).right.color, new Color(255,0,0));
  }
}