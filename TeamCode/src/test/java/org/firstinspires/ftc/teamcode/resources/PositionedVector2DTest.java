package org.firstinspires.ftc.teamcode.resources;

import static org.junit.Assert.assertEquals;
import com.broombots.sweep.Classes.Pos2D;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PositionedVector2DTest {

    @Mock
    HardwareMap hardwareMap;

    @Test
    public void ensureCorrectConstructorDefaults(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,0));
        assertEquals(1.0,vector2D.getMagnitude(),1e-7);
        assertEquals(0,vector2D.getDirectionDegrees(),1e-7);
        assertEquals(0, vector2D.getPos2D().x,1e-7);
        assertEquals(0,vector2D.getPos2D().y,1e-7);
    }
    @Test
    public void ensureCorrectConstructorRandom(){
        PositionedVector2D vector2D = new PositionedVector2D(32.783,new Pos2D(3.85,-274,-30.47));
        assertEquals(32.783,vector2D.getMagnitude(),1e-7);
        assertEquals(-30.47,vector2D.getDirectionDegrees(),1e-7);
        assertEquals(3.85, vector2D.getPos2D().x,1e-7);
        assertEquals(-274,vector2D.getPos2D().y,1e-7);
    }
    @Test
    public void translation(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,0));
        vector2D.translateBy(1,-1);
        assertEquals(1,vector2D.getPos2D().x,1e-7);
        assertEquals(-1,vector2D.getPos2D().y,1e-7);
        vector2D.translateBy(-7.5,3.1);
        assertEquals(-6.5,vector2D.getPos2D().x,1e-7);
        assertEquals(2.1,vector2D.getPos2D().y,1e-1);
    }
    @Test
    public void rotation(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,0));
        vector2D.rotateBy(90);
        assertEquals(90,vector2D.getDirectionDegrees(),1e-7);
        vector2D.rotateBy(-180);
        assertEquals(-90,vector2D.getDirectionDegrees(),1e-7);
        vector2D.setRotationTo(45);
        assertEquals(45,vector2D.getDirectionDegrees(),1e-7);
    }
    @Test
    public void translationAndRotation(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,0));
        vector2D.translateBy(1,-1);
        vector2D.rotateBy(90);
        assertEquals(1,vector2D.getPos2D().x,1e-7);
        assertEquals(-1,vector2D.getPos2D().y,1e-7);
        assertEquals(90,vector2D.getDirectionDegrees(),1e-7);
    }
    @Test
    public void scaling(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,0));
        vector2D.scaleBy(5);
        assertEquals(5,vector2D.getMagnitude(),1e-7);
        vector2D.scaleBy(0.5);
        assertEquals(2.5,vector2D.getMagnitude(),1e-7);
    }
    @Test
    public void translationRotationAndScaling(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,0));
        vector2D.translateBy(1,-1);
        vector2D.rotateBy(90);
        vector2D.scaleBy(5);
        assertEquals(5,vector2D.getMagnitude(),1e-7);
        assertEquals(1,vector2D.getPos2D().x,1e-7);
        assertEquals(-1,vector2D.getPos2D().y,1e-7);
        assertEquals(90,vector2D.getDirectionDegrees(),1e-7);
    }
    @Test
    public void translationRotationAndScalingRandom(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,0));
        vector2D.translateBy(-3.5,7.8);
        vector2D.rotateBy(-45);
        vector2D.scaleBy(3.14);
        assertEquals(3.14,vector2D.getMagnitude(),1e-7);
        assertEquals(-3.5,vector2D.getPos2D().x,1e-7);
        assertEquals(7.8,vector2D.getPos2D().y,1e-7);
        assertEquals(-45,vector2D.getDirectionDegrees(),1e-7);
    }
    @Test
    public void chainedOperations(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,0));
        vector2D.translateBy(-3.5,7.8);
        vector2D.rotateBy(-45);
        vector2D.scaleBy(3.14);
        assertEquals(3.14,vector2D.getMagnitude(),1e-7);
        assertEquals(-3.5,vector2D.getPos2D().x,1e-7);
        assertEquals(7.8,vector2D.getPos2D().y,1e-7);
        assertEquals(-45,vector2D.getDirectionDegrees(),1e-7);
    }
    @Test
    public void zeroMagnitude(){
        PositionedVector2D vector2D = new PositionedVector2D(0.0,new Pos2D(0,0,0));
        assertEquals(0.0,vector2D.getMagnitude(),1e-7);
        vector2D.scaleBy(5);
        assertEquals(0.0,vector2D.getMagnitude(),1e-7);
    }
    @Test
    public void zeroMagnitudeWithTranslationAndRotation(){
        PositionedVector2D vector2D = new PositionedVector2D(0.0,new Pos2D(0,0,0));
        vector2D.translateBy(1,-1);
        vector2D.rotateBy(90);
        assertEquals(0.0,vector2D.getMagnitude(),1e-7);
        assertEquals(1,vector2D.getPos2D().x,1e-7);
        assertEquals(-1,vector2D.getPos2D().y,1e-7);
        assertEquals(90,vector2D.getDirectionDegrees(),1e-7);
    }
    @Test
    public void infiniteMagnitude(){
        PositionedVector2D vector2D = new PositionedVector2D(Double.POSITIVE_INFINITY,new Pos2D(0,0,0));
        assertEquals(Double.POSITIVE_INFINITY,vector2D.getMagnitude(),1e-7);
        vector2D.scaleBy(5);
        assertEquals(Double.POSITIVE_INFINITY,vector2D.getMagnitude(),1e-7);
    }
    @Test
    public void infiniteMagnitudeWithTranslationAndRotation(){
        PositionedVector2D vector2D = new PositionedVector2D(Double.POSITIVE_INFINITY,new Pos2D(0,0,0));
        vector2D.translateBy(1,-1);
        vector2D.rotateBy(90);
        assertEquals(Double.POSITIVE_INFINITY,vector2D.getMagnitude(),1e-7);
        assertEquals(1,vector2D.getPos2D().x,1e-7);
        assertEquals(-1,vector2D.getPos2D().y,1e-7);
        assertEquals(90,vector2D.getDirectionDegrees(),1e-7);
    }
    @Test
    public void endpointPointTowards(){
        PositionedVector2D vector2D = new PositionedVector2D(5.0,new Pos2D(0,0,0));
        Pos2D endpoint = vector2D.getEndpoint();
        assertEquals(5.0,endpoint.x,1e-7);
        assertEquals(0.0,endpoint.y,1e-7);
    }
    @Test
    public void endpointPointTowardsWithRotation(){
        PositionedVector2D vector2D = new PositionedVector2D(5.0,new Pos2D(0,0,90));
        Pos2D endpoint = vector2D.getEndpoint();
        assertEquals(0.0,endpoint.x,1e-7);
        assertEquals(5.0,endpoint.y,1e-7);
    }
    @Test
    public void endpointPointTowardsWithTranslationAndRotation(){
        PositionedVector2D vector2D = new PositionedVector2D(5.0,new Pos2D(1,1,90));
        Pos2D endpoint = vector2D.getEndpoint();
        assertEquals(1.0,endpoint.x,1e-7);
        assertEquals(6.0,endpoint.y,1e-7);
    }
    @Test
    public void endpointAngleToPositionVertical(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(1,1,90));
        double angleToPosition = vector2D.getEndpointAngleToPosition(new Pos2D(1,6,0));
        assertEquals(90,angleToPosition,1e-7);
    }
    @Test
    public void endpointAngleToPositionHorizontal(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(1,1,0));
        double angleToPosition = vector2D.getEndpointAngleToPosition(new Pos2D(6,1,0));
        assertEquals(0,angleToPosition,1e-7);
    }
    @Test
    public void endpointAngleToPositionDiagonal(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(-1,-1,45));
        double angleToPosition = vector2D.getEndpointAngleToPosition(new Pos2D(6,6,0));
        assertEquals(45,angleToPosition,1e-7);
    }
    @Test
    public void endpointAngleToPositionNegative(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,90));
        double angleToPosition = vector2D.getEndpointAngleToPosition(new Pos2D(-1,0,0));
        assertEquals(-135,angleToPosition,1e-7);
    }
    @Test
    public void endpointAngleOnTarget(){
        PositionedVector2D vector2D = new PositionedVector2D(1.0,new Pos2D(0,0,90));
        double angleToPosition = vector2D.getEndpointAngleToPosition(new Pos2D(0,1,0));
        assertEquals(0,angleToPosition,1e-7);
    }
}
