import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ArrayList.*;
import java.util.Random;
import java.util.Random.*;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    //Images (variables para almacenar las imagenes usadas en el frame)
    Image backgroungImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth/8; //el bird queda en 1/8 del ancho de la ventana arrancando de la izquierda
    int birdY = boardHeight/2; //el pajaro queda a mitad de altura de la ventana
    int birdWidht = 34;
    int birdHeight = 24;


    class Bird //no es lo ideal meter una clase dentro de otra, pero se hace para acceder mas facil a alas variables
    {
        int x = birdX;
        int y = birdY;
        int width = birdWidht;
        int height = birdHeight;
        Image img;

        Bird (Image img){
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;

    int pipeWidht = 64;  //  scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int widht = pipeWidht;
        int height = pipeHeight;
        Image img;
        boolean passed = false; //para saber si el bird paso la columna

        Pipe (Image img){
            this.img = img;
        }

    }

    //game logic
    Bird bird;

    int velocityX = -4; //es lo que hace que las columnas se muevan hacia el bird (izquierda)

    int velocityY = 0;

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score =  0;

    int gravity = 1;

    ArrayList<Pipe> pipes; //necesitamos un arreglo para almacenar las columnas creadas que se van a usar en el juego
    Random random = new Random();






    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);

        setFocusable(true); //para que la KeyEvents tenga en cuenta al flappy bird
        addKeyListener(this); //esto chequea las 3 funciones de KeyEvent


        //load images
        backgroungImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);

        //pipes
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {  //cada 1,5 segundos se llama esta funcion
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer (SE necesita el gameloop para poder ir repintando las imagenes para que cambien los frames mientras se juega, sino quedaria simpre la misma imagen.)
        gameLoop = new Timer(1000/60, this); //60 veces por segundo (1000 es 1sec (esta en milisegundos))
                                                        // llama a la funcion this (que hace referencia a ActionPerformed, la funcion de la Interfaz ActionListener implementada)
        gameLoop.start();
    }

    public void placePipes(){
        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2)); // 0 - 128 - (numero entre 0-1)*256) // va a dar un numero entre 1/4 y 3/4 de pipeHeight

        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace; //esto va a posicioonar la columna inferior debajo de la columna superior dejando un espacio para que pase el bird
        pipes.add(bottomPipe);

    }

    //invocando funcion heredada de JPanel
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);

    }

    public void draw(Graphics g){
        //background
        g.drawImage(backgroungImg, 0,0,boardWidth, boardHeight,null); //cuando se dibuja una imagen(draw) se pasa primero la imagen, luego los puntos de inicio (x,y) que son 0 y 0 en este caso porque es la foto para el fondo, y luego las diemsiones de la imagen (ancho y largo);

        //bird
        g.drawImage(bird.img, bird.x, bird.y,bird.width, bird.height,null);

        //pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.widht, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver){
            g.drawString("Game Over : " + String.valueOf((int) score),80, 320);
        }
        else{
            g.drawString(String.valueOf((int)score),10,35);
        }
    }

    public void move(){
        //bird
        velocityY += gravity; //lo que hacemos con gravity es ir sumando un pixel por frame para que el bird caiga hacia abajo, sino eguiria para arriba hasta el techo;
        bird.y += velocityY; //se mueve -9 pixeles por frame (para arriba, ya que la posicion del bird es 0,0; para arriba es -y )
        bird.y = Math.max(bird.y,0);

        if (bird.y > boardHeight){
            gameOver = true;
        }

        //pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.widht){ //si la posicion del pajaro es mayor (mas a la derecha) que la posicion de la columna + la anchura
                pipe.passed = true;
                score += 0.5; //se suma 0,5 ya que al ser dos columnas, cada vez que las pase suma 1
            }

            if (collision(bird, pipe)){
                gameOver = true;
            }
        }
    }


    public boolean collision (Bird bird, Pipe pipe){ //retorna true si el bird esta en la posicion de la columna (funcion hardcodeada)
        return  bird.x < pipe.x + pipe.widht &&
                bird.x + bird.width > pipe.x &&
                bird.y < pipe.y + pipe.height &&
                bird.y + bird.height > pipe.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {  // esta funcion (que se implementa a partir de la super clase action listener) se ejecuta en cada frame, es decir 60 veces por segundo como esta seteado.
                                                    //lo que provoca el movimiento es ir cambiando de posicion las imagenes y refrescando los frames;
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    //utilizamos las funciones implementadas con KeyLitener, para poder asignarle teclas al movimiento del juego.

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){  //si el key event(tecla presionada) es la barra espaciadora(VK_SPACE)
            velocityY = -9;                        //la velocidad cambia a -9 (el bird sube 9 pixeles desde donde esta)
        if (gameOver){
            bird.y = birdY;
            velocityY = 0;
            pipes.clear();
            score = 0;
            gameOver = false;
            gameLoop.start();
            placePipesTimer.start();
        }
        }
    }
    //no precisamos usar estas
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }



}
