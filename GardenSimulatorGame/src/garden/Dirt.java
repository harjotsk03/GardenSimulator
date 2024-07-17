package garden;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import util.ImageLoader;

public class Dirt extends GardenObject {
    private int state = 0;
    private int vegetableState = 0;
    private String vegetable;
    private Timer vegetableTimer;
    private long timerStartTime;
    private int growthStage = 0;
    private boolean imageUpdated = false;
    private float alpha = 1.0f;

    private BufferedImage[] carrotImages = new BufferedImage[5];
    private BufferedImage[] tomatoImages = new BufferedImage[5];
    private BufferedImage[] cornImages = new BufferedImage[5];
    private BufferedImage[] lettuceImages = new BufferedImage[5];
    private BufferedImage currentImage;

    public Dirt(double x, double y, double s) {
        super(x, y, s);
        img = ImageLoader.loadImage("assets/dirt.png");
        carrotImages[0] = ImageLoader.loadImage("assets/carrot/carrot3.png");
        carrotImages[1] = ImageLoader.loadImage("assets/carrot/carrot2.png");
        carrotImages[2] = ImageLoader.loadImage("assets/carrot/carrot1.png");
        carrotImages[3] = ImageLoader.loadImage("assets/carrot/carrot4.png");
        carrotImages[4] = ImageLoader.loadImage("assets/carrot/carrot5.png");

        tomatoImages[0] = ImageLoader.loadImage("assets/tomato/tomato3.png");
        tomatoImages[1] = ImageLoader.loadImage("assets/tomato/tomato2.png");
        tomatoImages[2] = ImageLoader.loadImage("assets/tomato/tomato1.png");
        tomatoImages[3] = ImageLoader.loadImage("assets/tomato/tomato4.png");
        tomatoImages[4] = ImageLoader.loadImage("assets/tomato/tomato5.png");

        cornImages[0] = ImageLoader.loadImage("assets/corn/corn1.png");
        cornImages[1] = ImageLoader.loadImage("assets/corn/corn2.png");
        cornImages[2] = ImageLoader.loadImage("assets/corn/corn3.png");
        cornImages[3] = ImageLoader.loadImage("assets/corn/corn4.png");
        cornImages[4] = ImageLoader.loadImage("assets/corn/corn5.png");

        lettuceImages[0] = ImageLoader.loadImage("assets/lettuce/lettuce1.png");
        lettuceImages[1] = ImageLoader.loadImage("assets/lettuce/lettuce2.png");
        lettuceImages[2] = ImageLoader.loadImage("assets/lettuce/lettuce3.png");
        lettuceImages[3] = ImageLoader.loadImage("assets/lettuce/lettuce4.png");
        lettuceImages[4] = ImageLoader.loadImage("assets/lettuce/lettuce5.png");
    }

    @Override
    public void update() {
        if (vegetableState == 0) {
            vegetable = "none";
            imageUpdated = false;
            currentImage = null; // Ensure currentImage is reset when no vegetable
        } else if (vegetableState == 1) {
            vegetable = "carrot";
            if (!imageUpdated) {
                currentImage = carrotImages[0];
                imageUpdated = true;
            }
            if (vegetableTimer == null) {
                startVegetableTimer();
            }
        } else if (vegetableState == 2) {
            vegetable = "tomato";
            if (!imageUpdated) {
                currentImage = tomatoImages[0];
                imageUpdated = true;
            }
            if (vegetableTimer == null) {
                startVegetableTimer();
            }
        } else if (vegetableState == 3) {
            vegetable = "corn";
            if (!imageUpdated) {
                currentImage = cornImages[0];
                imageUpdated = true;
            }
            if (vegetableTimer == null) {
                startVegetableTimer();
            }
        } else if (vegetableState == 4) {
            vegetable = "lettuce";
            if (!imageUpdated) {
                currentImage = lettuceImages[0];
                imageUpdated = true;
            }
            if (vegetableTimer == null) {
                startVegetableTimer();
            }
        }

        // Handle growth stages and update currentImage accordingly
        if (growthStage == 4) {
            if (vegetable.equals("carrot")) {
                currentImage = carrotImages[4];
            } else if (vegetable.equals("tomato")) {
                currentImage = tomatoImages[4];
            }else if (vegetable.equals("corn")){
                currentImage = cornImages[4];
            }else if(vegetable.equals("lettuce")){
                currentImage = lettuceImages[4];
            }
        }
    }

    private void startVegetableTimer() {
        if (vegetableTimer != null && vegetableTimer.isRunning()) {
            vegetableTimer.stop();
        }

        timerStartTime = System.currentTimeMillis();

        vegetableTimer = new Timer(500, new ActionListener() { // 0.5 seconds for fading
            @Override
            public void actionPerformed(ActionEvent e) {
                growVegetable();
            }
        });
        vegetableTimer.setRepeats(true);
        vegetableTimer.start();
    }

    private void growVegetable() {
        long elapsedTime = System.currentTimeMillis() - timerStartTime;
        if (vegetable.equals("carrot")) {
            if (elapsedTime > 5000 && growthStage == 0) {
                currentImage = carrotImages[1];
                growthStage = 1;
            } else if (elapsedTime > 10000 && growthStage == 1) {
                currentImage = carrotImages[2];
                growthStage = 2;
            } else if (elapsedTime > 15000 && growthStage == 2) {
                currentImage = carrotImages[3];
                growthStage = 3;
                vegetableTimer.stop();
            }
        } else if (vegetable.equals("tomato")) {
			if (elapsedTime > 5000 && growthStage == 0) {
                currentImage = tomatoImages[1];
                growthStage = 1;
            } else if (elapsedTime > 10000 && growthStage == 1) {
                currentImage = tomatoImages[2];
                growthStage = 2;
            } else if (elapsedTime > 15000 && growthStage == 2) {
                currentImage = tomatoImages[3];
                growthStage = 3;
                vegetableTimer.stop();
            }
        } else if (vegetable.equals("corn")) {
			if (elapsedTime > 5000 && growthStage == 0) {
                currentImage = cornImages[1];
                growthStage = 1;
            } else if (elapsedTime > 10000 && growthStage == 1) {
                currentImage = cornImages[2];
                growthStage = 2;
            } else if (elapsedTime > 15000 && growthStage == 2) {
                currentImage = cornImages[3];
                growthStage = 3;
                vegetableTimer.stop();
            }
        } else if (vegetable.equals("lettuce")) {
			if (elapsedTime > 5000 && growthStage == 0) {
                currentImage = lettuceImages[1];
                growthStage = 1;
            } else if (elapsedTime > 10000 && growthStage == 1) {
                currentImage = lettuceImages[2];
                growthStage = 2;
            } else if (elapsedTime > 15000 && growthStage == 2) {
                currentImage = lettuceImages[3];
                growthStage = 3;
                vegetableTimer.stop();
            }
        }
    }

    public void setVegetableState(int state) {
        vegetableState = state;
        growthStage = 0;
    }

    @Override
    public void drawObject(Graphics2D g2) {
        AffineTransform transform = g2.getTransform();
        g2.translate(pos.x, pos.y);
        g2.scale(sca, sca);

        g2.drawImage(img, -img.getWidth() / 2, -img.getHeight() / 2, null);

        if (currentImage != null && vegetableState != 0) {
            g2.drawImage(currentImage, -currentImage.getWidth() / 2, -currentImage.getHeight() / 2, null);
        }

        g2.setTransform(transform);
    }

    public void setDirtImg(int dirtState) {
        if (dirtState == 0) {
            this.state = 0;
            img = ImageLoader.loadImage("assets/dirt.png");
            imageUpdated = false;
        } else if (dirtState == 1) {
            this.state = 1;
            img = ImageLoader.loadImage("assets/dirtPlant.png");
        }
    }

    public int getDirtState() {
        return state;
    }

    @Override
    protected void setOutlineShape() {
        areaShape = new Rectangle2D.Double(-img.getWidth() / 2, -img.getHeight() / 2, img.getWidth(), img.getHeight());
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public void setGrowthStage(int i) {
        growthStage = i;
    }
}
