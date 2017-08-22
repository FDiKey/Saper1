

import com.sun.prism.*;
import com.sun.prism.Image;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Random;

public class Game extends JFrame
{
    final String TITEL = "GAME MINE";
    final String FLAG = "f";
    final int BLOCK_SIZE = 30;
    final int FIELD_SIZE = 15;
    final int COUNT_MINE = 30;
    final int FIELD_DX = 6;
    final int FIELD_DY =  28;
    final int START_POSITION = 200;
    final int MOUSE_BUTTON_LEFT = 1;
    final int MOUSE_BUTTON_RIGHT = 3;
    final int[] COLOR_OF_NUMBERS = {0x0000FF,0x00FF00, 0xFF0000,0x0};
    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];
    Random random = new Random();
    int countOpenCells;

    boolean Win;
    boolean Lose;
    int bangX, bangY;

    //final Image qwer = ImageIO.read(new File("1.png"));;






    public static void main(String[] args)
    {
        new Game();
    }

    Game ()
    {
        setTitle(TITEL);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(START_POSITION, START_POSITION, BLOCK_SIZE * FIELD_SIZE + FIELD_DX, BLOCK_SIZE * FIELD_SIZE + FIELD_DY);
        setResizable(false);
        Canvas canvas = new Canvas();
        canvas.setBackground(Color.white);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX() / BLOCK_SIZE;
                int y = e.getY() / BLOCK_SIZE;

                if(e.getButton() == MOUSE_BUTTON_LEFT && !Win && !Lose)
                {
                    if(field[x][y].isNotOpen())
                    {
                        OpenCells(x,y);
                        Win = countOpenCells == FIELD_SIZE * FIELD_SIZE - COUNT_MINE;

                        if(Lose)
                        {
                            bangX = x;
                            bangY = y;

                        }

                    }
                }
                if(e.getButton() == MOUSE_BUTTON_RIGHT) field[x][y].inversFlag();

                canvas.repaint();
            }
        });
        add(BorderLayout.CENTER, canvas);
        setVisible(true);
        initField();

    }

    void OpenCells(int x, int y)
    {

        if(x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return;
        if(!field[x][y].isNotOpen()) return;
        field[x][y].open();
        if(field[x][y].getICountOfMine() > 0 || Lose) return;

        for(int dx = -1; dx < 2; dx++)
            for(int dy = -1; dy < 2; dy ++) OpenCells(x + dx, y + dy);



    }

    void initField ()
    {
        int x , y, countMine = 0;

        for(x = 0; x < FIELD_SIZE; x++)
            for(y = 0; y < FIELD_SIZE; y++)
                field[x][y] = new Cell();


        while(countMine < COUNT_MINE)
        {
            do{
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);

            }while(field[y][x].isMine());

            field[y][x].mine();

            countMine++;
            System.out.println(countMine);
        }
        for(x = 0; x < FIELD_SIZE; x++)
            for(y = 0; y < FIELD_SIZE; y++)
            {
                System.out.println(x);
                if(!field[y][x].isMine())
                {
                    int count = 0;
                    for(int dx = -1; dx < 2; dx++)
                        for(int dy = -1; dy < 2; dy ++) {
                            int nX = x + dx;
                            int nY = y + dy;

                            if(nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE -1)
                            {
                                nX = x;
                                nY = y;
                            }

                            count += (field[nY][nX].isMine()) ? 1 : 0;
                        }
                    field[y][x].setICountOfMine(count);

                }
            }
    }

    class Cell
    {
        private boolean isOpen = false;
        private boolean isMine = false;
        private boolean isFlag = false;
        private int iCountOfMine;


        void mine() {isMine = true;  }
        void open() {
            isOpen = true;
            Lose = isMine;
            if(!Lose) countOpenCells++;
        }

        void inversFlag () { isFlag = !isFlag; }

        boolean isNotOpen() { return !isOpen; }

        boolean isMine() { return isMine; }

        void setICountOfMine(int countOfMine) { this.iCountOfMine = countOfMine;}

        int getICountOfMine() { return iCountOfMine;}

        void paintBomb(Graphics g, int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x*BLOCK_SIZE + 7, y * BLOCK_SIZE + 10, 18,10);
            g.fillRect(x*BLOCK_SIZE + 11, y * BLOCK_SIZE + 6, 10,18);
            g.fillRect(x*BLOCK_SIZE + 9, y * BLOCK_SIZE + 8, 14,14);
            g.setColor(Color.WHITE);
            g.fillRect(x*BLOCK_SIZE + 11, y * BLOCK_SIZE + 10, 4, 4);

        }

        void paintString (Graphics g, String str, int x, int y, Color color)
        {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x * BLOCK_SIZE + 8, y * BLOCK_SIZE + 26);
            //g.drawString(Integer.toString(iCountOfMine), x * BLOCK_SIZE + 8, y * BLOCK_SIZE + 26);
        }

        void paint (Graphics g, int x, int y) {
            g.setColor(Color.black);
            g.drawRect(x*BLOCK_SIZE,y*BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE);

            if(!isOpen) {

                if((Win || Lose) && isMine) paintBomb( g, x, y, Color.BLACK);
                else {

                    g.setColor(Color.WHITE.darker());
                    g.draw3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);

                    if(isFlag) paintString(g, FLAG, x, y, Color.RED);
                }
            } else {

                if(isMine) paintBomb(g, x, y, Lose ? Color.red : Color.BLACK);
                else
                if(iCountOfMine > 0) paintString(g, Integer.toString(iCountOfMine), x, y, new Color(COLOR_OF_NUMBERS[iCountOfMine - 1]));


            }



        }
    }

    class Canvas extends JPanel
    {



        @Override
        public void paint(Graphics g)
        {
            super.paint(g);
            for(int x = 0; x < FIELD_SIZE; x++)
                for(int y =0; y < FIELD_SIZE; y++)
                {
                    field[y][x].paint(g,y,x);
                }
        }
    }
}