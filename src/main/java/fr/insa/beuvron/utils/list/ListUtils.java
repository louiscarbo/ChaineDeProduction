/*
    Copyright 2000-2014 Francois de Bertrand de Beuvron

    This file is part of UtilsBeuvron.

    UtilsBeuvron is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UtilsBeuvron is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UtilsBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.beuvron.utils.list;

import fr.insa.beuvron.utils.StringUtil;
import java.util.List;

/**
 *
 * @author francois
 */
public class ListUtils {

    @FunctionalInterface
    public interface ElemFormatter<E> {

        public String format(E elem);
    }

    public static String formatList(List<? extends Object> list,
            String debut, String fin, String separateur) {
        return formatList(list, debut, fin, separateur, Object::toString);
    }

    public static <E> String formatList(List<? extends E> list,
            String debut, String fin, String separateur,
            ElemFormatter<E> formatter) {
        StringBuilder res = new StringBuilder();
        res.append(debut);
        for (int i = 0; i < list.size(); i++) {
            res.append(formatter.format(list.get(i)));
            if (i != list.size() - 1) {
                res.append(separateur);
            }
        }
        res.append(fin);
        return res.toString();
    }

    public static <E> String enumerateList(List<? extends E> list,
            String beforeNum, int debutNumerotation, String betweenNumAndVal, String afterVal,
            ElemFormatter<E> formatter) {
        StringBuilder res = new StringBuilder();
        if (list.size() != 0) {
            int nbrDigit = (int) Math.floor(Math.log10(list.size())) + 1;
            String beforeOtherLines = StringUtil.mult(" ", beforeNum.length() + nbrDigit) + betweenNumAndVal;
            for (int i = 0; i < list.size(); i++) {
                String beforeFirstLine = beforeNum
                        + String.format("%" + nbrDigit + "d", (i + debutNumerotation))
                        + betweenNumAndVal;
                res.append(StringUtil.specialIndent(formatter.format(list.get(i)), beforeFirstLine, beforeOtherLines));
                res.append(afterVal);
            }
        }
        return res.toString();
    }

    public static String enumerateList(List<? extends Object> list) {
        return enumerateList(list, "", 1, " : ", "\n", Object::toString);
    }
    
    public static <E> String enumerateList(List<? extends E> list,ElemFormatter<E> formatter) {
        return enumerateList(list, "", 1, " : ", "\n", formatter);
    }
    
//    public static <E> E selectOne(List<E> list,ElemFormatter<F super E> formatter) {
//        int rep = -1;
//        while (rep < 0 || rep > semestres.size()) {
//            System.out.println("------ choix du semestre :");
//            System.out.println(ListUtils.enumerateList(semestres, "  ", 1, " : ", "\n", Semestre::getNom));
//            rep = ConsoleFdB.entreeInt("votre choix (0 pour annuler) : ");
//        }
//        if (rep == 0) {
//            return Optional.empty();
//        } else {
//            return Optional.of(semestres.get(rep - 1));
//        }
//        
//    }

}
