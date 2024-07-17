package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.Timer;
import ddf.minim.Minim;
import garden.Garden;
import tools.Carrot;
import tools.Corn;
import tools.Digger;
import tools.Lettuce;
import tools.Shovel;
import tools.SidebarTool;
import tools.Tomato;
import tools.WaterCan;
import garden.Dirt;
import garden.Fence;
import ui.Instructions;
import ui.Sidebar;
import javax.swing.JFrame;
import javax.swing.JPanel;
import util.MinimHelper;
import util.Util;

public class GardenPanel extends JPanel implements ActionListener {
    public static int W_WIDTH = 1280;
    public static int W_HEIGHT = 832;

    private double mouseX;
    private double mouseY;

    private Garden garden;
    private ArrayList<Dirt> DirtArr;
    private ArrayList<Fence> FenceArr;

    private Instructions instructions = null;
    private Sidebar sidebar;
    private Shovel pick;
    private Tomato tomato;
    private Carrot carrot;
    private Digger digger;
    private Corn corn;
    private Lettuce lettuce;
    private WaterCan waterCan;

    private int sidebarX = W_WIDTH - 60;
    private int sidebarYStart = 200;
    private int spacing = 75;

    private int light = 0;
    private boolean isDay = true;
    private int timeOfDay = 600; // Start at 10:00 AM (10 * 60)
    private Timer timer;

    private boolean hasWatered = false;

    @SuppressWarnings("unused")
    private Minim minim;

    GardenPanel(JFrame frame) {
        this.setBackground(Color.white);
        setPreferredSize(new Dimension(W_WIDTH, W_HEIGHT));

        garden = new Garden("assets/garden.png");

        sidebar = new Sidebar(W_WIDTH - 60, W_HEIGHT / 2, 1);

        pick = new Shovel(sidebarX, sidebarYStart, 0.7);
        tomato = new Tomato(sidebarX, sidebarYStart + spacing, 0.9);
        carrot = new Carrot(sidebarX, sidebarYStart + 2 * spacing, 1);
        lettuce = new Lettuce(sidebarX, sidebarYStart + 3 * spacing, 1);
        corn = new Corn(sidebarX, sidebarYStart + 4 * spacing, 1);
        waterCan = new WaterCan(sidebarX, sidebarYStart + 5 * spacing, 1);
        digger = new Digger(sidebarX, sidebarYStart + 6 * spacing, 0.7);

        DirtArr = new ArrayList<>();
        FenceArr = new ArrayList<>();

        createGarden(4, 3);

        minim = new Minim(new MinimHelper());

        MyMouseListener ml = new MyMouseListener();
        addMouseListener(ml);

        MyMouseMotionListener mml = new MyMouseMotionListener();
        addMouseMotionListener(mml);

        timer = new Timer(30, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        garden.drawGarden(g2);
        for (int i = 0; i < DirtArr.size(); i++) {
            DirtArr.get(i).drawObject(g2);
        }
        for (int i = 0; i < FenceArr.size(); i++) {
            FenceArr.get(i).drawObject(g2);
        }

        g2.setColor(new Color(0, 0, 0, light));
        g2.fillRect(0, 0, W_WIDTH, W_HEIGHT);

        // SIDEBAR
        sidebar.drawSidebar(g2);
        pick.drawObject(g2);
        tomato.drawObject(g2);
        carrot.drawObject(g2);
        lettuce.drawObject(g2);
        corn.drawObject(g2);
        waterCan.drawObject(g2);
        digger.drawObject(g2);
        if(instructions != null){
            instructions.displayInstruction(g2);
        }

        // Convert timeOfDay to hours and minutes
        int hours = timeOfDay / 60;
        int minutes = timeOfDay % 60;

        // Format time as HH:MM
        String timeString = String.format("%02d:%02d", hours, minutes);

        // Draw time
        g2.setColor(Color.WHITE);
        g2.drawString(timeString, 50, 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (pick.getMouseFollowing()) {
            pick.setPos(mouseX, mouseY);
        }
        if (tomato.getMouseFollowing()) {
            tomato.setPos(mouseX, mouseY);
        }
        if (carrot.getMouseFollowing()) {
            carrot.setPos(mouseX, mouseY);
        }
        if (digger.getMouseFollowing()) {
            digger.setPos(mouseX, mouseY);
        }
        if (corn.getMouseFollowing()) {
            corn.setPos(mouseX, mouseY);
        }
        if (lettuce.getMouseFollowing()) {
            lettuce.setPos(mouseX, mouseY);
        }
        if (waterCan.getMouseFollowing()) {
            waterCan.setPos(mouseX, mouseY);
        }

        for (int i = 0; i < DirtArr.size(); i++) {
            Dirt currentDirt = DirtArr.get(i);
            currentDirt.update();

            if (currentDirt.getDirtState() == 0) {
                if (currentDirt.isColliding(pick)) {
                    currentDirt.setDirtImg(1);
                }
            }

            if (currentDirt.getGrowthStage() == 3) {
                if (currentDirt.isColliding(digger)) {
                    currentDirt.setGrowthStage(4);
                }
            }

            if (currentDirt.getDirtState() == 1) {
                if (currentDirt.isColliding(carrot)) {
                    currentDirt.setVegetableState(1);
                } else if (currentDirt.isColliding(tomato)) {
                    currentDirt.setVegetableState(2);
                } else if (currentDirt.isColliding(corn)) {
                    currentDirt.setVegetableState(3);
                } else if (currentDirt.isColliding(lettuce)) {
                    currentDirt.setVegetableState(4);
                }
            }
        }

        if (light >= 200) {
            isDay = false;
        } else if (light < 0) {
            isDay = true;
        }


        int incr = 1;
        if (timeOfDay >= 1200 || timeOfDay < 400) {
            // Night time
            incr = 5;
            light+= 2;
            hasWatered = false;
        } else {
            light-= 2;
        }
    
        if (light > 255) {
            light = 255;
        } else if (light < 0) {
            light = 0;
        }

        if(!hasWatered && timeOfDay == 840){
            instructions = new Instructions(W_WIDTH / 2 - 100, W_HEIGHT - 100, "Water your garden!");
            timeOfDay = timeOfDay;
        }else{
            timeOfDay = (timeOfDay + incr) % (24 * 60);
        }
        
        repaint();
    }

    public class MyMouseListener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();

            if(waterCan.clicked(mouseX, mouseY)){
                hasWatered = true;
                instructions = null;
            }

            moveTool(pick, e, sidebarYStart);

            moveTool(tomato, e, sidebarYStart + spacing);

            moveTool(carrot, e, sidebarYStart + (2 * spacing));

            moveTool(lettuce, e, sidebarYStart + (3 * spacing));

            moveTool(corn, e, sidebarYStart + (4 * spacing));

            moveTool(waterCan, e, sidebarYStart + (5 * spacing));

            moveTool(digger, e, sidebarYStart + (6 * spacing));

        }
    }

    public class MyMouseMotionListener extends MouseMotionAdapter {
        public void mouseMoved(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    private void createGarden(int col, int row) {
        int widthBetween = 250;
        int heightBetween = 200;

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                Util.generateGarden(110 + (i * widthBetween), 115 + (j * heightBetween), DirtArr, 160 + (i * widthBetween), 145 + (j * heightBetween), FenceArr);
            }
        }

    }

    private void moveTool(SidebarTool object, MouseEvent e, float height) {
        if (object.clicked(mouseX, mouseY)) {
            if (e.isShiftDown()) {
                object.setMouseFollowing(false);
                object.setPos(W_WIDTH - 60, height);
            } else {
                object.setMouseFollowing(true);
                object.setPos(mouseX, mouseY);
            }
        }
    }
}
