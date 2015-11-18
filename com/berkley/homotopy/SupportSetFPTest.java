package com.berkley.homotopy;
/*
% Java code for Homotopy Fixed-Point l1-minimization

% Copyright ©2010. The Regents of the University of California (Regents).
% All Rights Reserved. Contact The Office of Technology Licensing,
% UC Berkeley, 2150 Shattuck Avenue, Suite 510, Berkeley, CA 94720-1620,
% (510) 643-7201, for commercial licensing opportunities.

% Written by by Victor Shia, Allen Y. Yang, Department of EECS, University of California,
% Berkeley.

% IN NO EVENT SHALL REGENTS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
% SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
% ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
% REGENTS HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

% REGENTS SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED
% TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
% PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF ANY,
% PROVIDED HEREUNDER IS PROVIDED "AS IS". REGENTS HAS NO OBLIGATION TO
% PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

import com.berkley.homotopy.jamafp.SupportSet;

public class SupportSetFPTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SupportSet s1 = new SupportSet(10);
		
		s1.addElement(10);
		s1.printSet();
		s1.addElement(12);
		s1.printSet();
		
		int[] a1 = new int[2];
		a1[0] = 2;
		a1[1] = 199;
		s1.addElements(a1);
		s1.printSet();
		
		s1.deleteIndex(2);
		s1.printSet();
		
		System.out.println(s1.find(12, 2)[0]);
		
		s1 = new SupportSet(10);
		s1.addElement(2);
		s1.addElement(5);
		s1.addElement(8);
		
		a1[1] = 0;
		s1.printSet();
		SupportSet.setDiffAndUnion(s1, a1, 15).printSet();
	}
}
