package Client;
import javax.swing.JButton;
import javax.swing.JFrame;


/**
 * Created by nori on 2017-03-01.
 */
public class FrameExam
{
    JFrame frame = new JFrame("������");
    JButton button = new JButton("������");

    public void createFrame()
    {
        //�����ӿ� ������Ʈ �߰�
        frame.add(button);

        //������ ũ�� ����
        frame.setSize(300, 600);

        //������ ���̱�
        frame.setVisible(true);

        //swing���� �ִ� X��ư Ŭ���� ����
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args)
    {
        //������ ����
        FrameExam frameExam = new FrameExam();
        frameExam.createFrame();
    }
}