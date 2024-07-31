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
import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.Timer;
import ddf.minim.Minim;
import fenceDecorator.BagDecor;
import fenceDecorator.SimpleFence;
import fenceDecorator.WoodDecor;
import fenceDecorator.FenceDecorInterface;
import fenceDecorator.FenceDecor;
import garden.Garden;
import garden.Sparkle;
import sidebar.Carrot;
import sidebar.Corn;
import sidebar.Digger;
import sidebar.Lettuce;
import sidebar.Shovel;
import sidebar.SidebarTool;
import sidebar.SimpleSidebar;
import sidebar.Tomato;
import sidebar.WaterCan;
import garden.Dirt;
import garden.Fence;
import ui.Button;
import ui.Instructions;
import ui.IntoScreen;
import ui.PausedScreen;
import ui.SellingScreen;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import util.MinimHelper;
import util.Util;

public class GardenPanel extends JPanel implements ActionListener {
    public final static int W_WIDTH = 1280;
    public final static int W_HEIGHT = 832;
    

    private double mouseX;
    private double mouseY;

    private Garden garden;
    private ArrayList<Dirt> DirtArr;
    private ArrayList<Fence> FenceArr;

    private Instructions instructions = null;
    private SimpleSidebar sidebar;
    private Shovel pick;
    private Tomato tomato;
    private Carrot carrot;
    private Digger digger;
    private Corn corn;
    private Lettuce lettuce;
    private WaterCan waterCan;
    private Sparkle cloud = null;

    private int sidebarX = W_WIDTH - 60;
    private int sidebarYStart = 200;
    private int spacing = 75;

    private int light = 0;
    private boolean isDay = true;
    private int timeOfDay = 600;
    private Timer timer;
    private int day = 1;

    private int gameState = 0;
    private Boolean paused = false;
    private Button pausePlayButton;

    private boolean hasWatered = false;
    private boolean hasDug = false;
    private boolean hasPlanted = false;
    private boolean toolActive = false;

    private int carrotReady = 0;
    private int lettuceReady = 0;
    private int cornReady = 0;
    private int tomatoReady = 0;

    FenceDecorInterface fence;

    private boolean drawSparkle = false;

    @SuppressWarnings("unused")
    private Minim minim;

    private IntoScreen introScreen;
    private PausedScreen pausedScreen;
    private SellingScreen sellingScreen;

    private JFrame frame;

    GardenPanel(JFrame frame) {
        this.frame = frame;
        this.setBackground(Color.white);
        setPreferredSize(new Dimension(W_WIDTH, W_HEIGHT));

        garden = new Garden("assets/garden.png");

        sidebar = new SimpleSidebar(W_WIDTH - 60, W_HEIGHT / 2, 1);

        pick = new Shovel(sidebarX, sidebarYStart, 0.7);
        tomato = new Tomato(sidebarX, sidebarYStart + spacing, 0.9);
        carrot = new Carrot(sidebarX, sidebarYStart + 2 * spacing, 1);
        lettuce = new Lettuce(sidebarX, sidebarYStart + 3 * spacing, 1);
        corn = new Corn(sidebarX, sidebarYStart + 4 * spacing, 1);
        waterCan = new WaterCan(sidebarX, sidebarYStart + 5 * spacing, 1);
        digger = new Digger(sidebarX, sidebarYStart + 6 * spacing, 0.7);
        pausePlayButton = new Button(10, 10);

        DirtArr = new ArrayList<>();
        FenceArr = new ArrayList<>();

        createGarden(4, 3);

        minim = new Minim(new MinimHelper());

        MyMouseListener ml = new MyMouseListener();
        addMouseListener(ml);

        MyMouseMotionListener mml = new MyMouseMotionListener();
        addMouseMotionListener(mml);

        introScreen = new IntoScreen(W_WIDTH, W_HEIGHT);
        pausedScreen = new PausedScreen(W_WIDTH, W_HEIGHT);

            JButton simpleFence = new JButton("Empty Bays");
            add(simpleFence);
            simpleFence.addActionListener(this);

            JButton woodDecor = new JButton("Add Wood");
            add(woodDecor);
            woodDecor.addActionListener(this);

            JButton bagDecor = new JButton("Add Bags");
            add(bagDecor);
            bagDecor.addActionListener(this);

            JButton fullBays = new JButton("Full Bays");
            add(fullBays);
            fullBays.addActionListener(this);

        fence = new SimpleFence(300, W_HEIGHT - 100);

        timer = new Timer(30, this);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform originalTransform = g2.getTransform(); // Save the original transform

        if (gameState == 0) {
            introScreen.drawIntroScreen(g2);
        } else if (gameState == 1) {

            garden.drawGarden(g2);
            for (Dirt dirt : DirtArr) {
                dirt.drawObject(g2);
            }
            for (Fence fence : FenceArr) {
                fence.drawObject(g2);
            }

            fence.showFence(g2);

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
        

            if (instructions != null) {
                instructions.displayInstruction(g2);
            }

            g2.setTransform(originalTransform);

            int hours = timeOfDay / 60;
            int minutes = timeOfDay % 60;

            String timeString = String.format("%02d:%02d", hours, minutes);

            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.WHITE);
            int stringWidth = g2.getFontMetrics().stringWidth(timeString);
            g2.drawString(timeString, W_WIDTH - stringWidth - 50, 50);
            g2.drawString("Day: " + day, W_WIDTH - 50 - stringWidth, 80);

            g2.setFont(new Font("Arial", Font.PLAIN, 15));
            g2.drawString("Carrots: " + carrotReady, W_WIDTH - 150 - stringWidth, 50);
            g2.drawString("Lettuce: " + lettuceReady, W_WIDTH - 150 - stringWidth, 80);
            g2.drawString("Tomato: " + tomatoReady, W_WIDTH - 150 - stringWidth, 110);
            g2.drawString("Corn: " + cornReady, W_WIDTH - 150 - stringWidth, 140);

            g2.setTransform(originalTransform);

            if(paused){
                pausedScreen.drawPausedScreen(g2);
            }

            pausePlayButton.drawButton(g2);
        }else if(gameState == 2){
            sellingScreen.drawSellingScreen(g2);
        }
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {

        if(gameState == 1){
            for(int i = 0; i < DirtArr.size(); i++){
                Dirt currentDirt = DirtArr.get(i);
    
                int counter = 0;
    
                if(currentDirt.getDirtState() == 1){
                    counter++;
                }
    
                if(counter > 0){
                    hasDug = true;
                    instructions = null;
                }
            }
    
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
                        if(currentDirt.getVegetableState() == 1){
                            carrotReady ++;
                        }else if(currentDirt.getVegetableState() == 2){
                            tomatoReady ++;
                        }else if(currentDirt.getVegetableState() == 3){
                            cornReady ++;
                        }else if(currentDirt.getVegetableState() == 4){
                            lettuceReady ++;
                        }
                    }
                }
    
                if (currentDirt.getDirtState() == 1) {
                    if (currentDirt.isColliding(carrot)) {
                        currentDirt.setVegetableState(1);
                        currentDirt.setDayOfPlant(day);
                    } else if (currentDirt.isColliding(tomato)) {
                        currentDirt.setVegetableState(2);
                        currentDirt.setDayOfPlant(day);
                    } else if (currentDirt.isColliding(corn)) {
                        currentDirt.setVegetableState(3);
                        currentDirt.setDayOfPlant(day);
                    } else if (currentDirt.isColliding(lettuce)) {
                        currentDirt.setVegetableState(4);
                        currentDirt.setDayOfPlant(day);
                    }
                }
            }
    
            for(int i=0; i < DirtArr.size(); i++){
                Dirt currentDirt = DirtArr.get(i);
                int plantedPlaces = 0;
                if(currentDirt.getVegetableState() != 0){
                    plantedPlaces++;
                }
    
                if(plantedPlaces != 0){
                    hasPlanted = true;
                }
            }
    
            if (light >= 200) {
                isDay = false;
            } else if (light < 0) {
                isDay = true;
            }
    
    
            int incr = 1;
            if (timeOfDay >= 1260 || timeOfDay < 240) {
                incr = 5;
                light+= 3;
                hasWatered = false;
            } else {
                light-= 3;
            }
        
            if (light > 255) {
                light = 255;
            } else if (light < 0) {
                light = 0;
            }
    
            if(!hasDug){
                instructions = new Instructions(W_WIDTH / 2 + 200, W_HEIGHT - 100, "Grab the shovel and dig the mud to prepare for planting!", "shovel");
                timeOfDay = timeOfDay;
            }
            else if(!hasWatered && hasPlanted && timeOfDay == 840){
                instructions = new Instructions(W_WIDTH / 2 + 200, W_HEIGHT - 100, "Water your garden!", "waterCan");
                timeOfDay = timeOfDay;
            }else{
                timeOfDay = (timeOfDay + incr) % (24 * 60);
            }
    
    
            if(timeOfDay == 840){
                cloud = null;
            }
    
            if(timeOfDay == 0){
                day++;
                for(int i = 0; i < DirtArr.size();  i++){
                    DirtArr.get(i).addDay();
                }
            }

            if (e.getActionCommand() == "Empty Bays") {
                fence = new SimpleFence(300, W_HEIGHT - 100);
            }
            
            if (e.getActionCommand() == "Add Bags") {
                FenceDecorInterface baseFence = new SimpleFence(300, W_HEIGHT - 100);
                fence = new BagDecor(300, W_HEIGHT - 100, baseFence);
            }

            if (e.getActionCommand() == "Add Wood") {
                FenceDecorInterface baseFence = new SimpleFence(300, W_HEIGHT - 100);
                fence = new WoodDecor(300, W_HEIGHT - 100, baseFence);
            }

            if (e.getActionCommand() == "Full Bays") {
                FenceDecorInterface baseFence = new BagDecor(300, W_HEIGHT - 100, new SimpleFence(300, W_HEIGHT - 100));
                fence = new WoodDecor(300, W_HEIGHT - 100, baseFence);
            }

            pausePlayButton.update();
        }

        if(carrotReady > 12 || lettuceReady > 12 || tomatoReady > 12 || cornReady > 12){
            this.paused = true;
        }

        repaint();
    }

    public class MyMouseListener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        
            if (introScreen.clicked(mouseX, mouseY)) {
                timer.start();
                gameState = 1;
            }

            // if (pausePlayButton.clicked(mouseX, mouseY)) {
            //     if (paused) {
            //         pausePlayButton.setState(1);
            //         timer.start();
            //         paused = false;
            //     } else {
            //         pausePlayButton.setState(2);
            //         timer.stop();
            //         paused = true;
            //     }
            //     repaint();
            // }

            if(paused){
                if(pausedScreen.clicked(mouseX, mouseY)){
                    restartApplication();
                }
            }
        
            if (gameState == 1) {
                if (waterCan.clicked(mouseX, mouseY)) {
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
                toolActive = false;
                object.setMouseFollowing(false);
                object.setPos(W_WIDTH - 60, height);
            } else {
                if(!toolActive){
                    toolActive = true;
                    object.setMouseFollowing(true);
                    object.setPos(mouseX, mouseY);
                }
            }
        }
    }

    private void restartApplication() {
        // Dispose of the current frame
        frame.dispose();

        // Create a new JFrame
        JFrame newFrame = new JFrame("Garden Game");
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(W_WIDTH, W_HEIGHT);
        newFrame.setResizable(false);
        newFrame.add(new GardenPanel(newFrame));
        newFrame.setVisible(true);
    }
}
