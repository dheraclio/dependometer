/*
 * Valtech Public L I C E N S E  (VPL) 1.0.2
 * 
 * dependometer
 * Copyright � 2007 Valtech GmbH
 *
 * dependometer software is made available free of charge under the
 * following conditions.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1.1.All copies and redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the following disclaimer.
 *
 * 1.2.Redistributions in binary form must reproduce the above copyright 
 * notice, this list of conditions and the following disclaimer in the 
 * documentation and/or other materials provided with the distribution. 
 * 
 * 1.3.The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: 
 * This product includes software developed by Valtech http://www.valtech.de/.
 * This acknowledgement must appear in the software itself, if and wherever 
 * such third-party acknowledgments normally appear.
 *
 * 1.4.The names "Valtech" and "dependometer" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact kmc@valtech.de <mailto:kmc@valtech.de> 
 *
 * BECAUSE THIS SOFTWARE IS LICENSED FREE OF CHARGE IT IS PROVIDED "AS IS" AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL VALTECH GMBH OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * LEGAL LIABILITY PROVIDED UNDER GERMAN LAW FOR INTENDED DAMAGES, BAD FAITH OR
 * GROSS NEGLIGENCE REMAINS UNAFFECTED.
 */

package com.valtech.source.dependometer.util;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class StringArray
{
    private final static int INITIAL_SIZE = 200;
    private final static int GROW_FACTOR = 2;

    private final int m_InitialSize;
    private final int m_GrowFactor;
    private int m_Size;
    private String[] m_Strings;
    private int m_NumberOfStrings;

    public StringArray()
    {
        m_InitialSize = INITIAL_SIZE;
        m_GrowFactor = GROW_FACTOR;

        clear();
    }

    public StringArray(int initialSize, int growFactor)
    {
        assert initialSize > 0;
        assert growFactor > 1;

        m_InitialSize = initialSize;
        m_GrowFactor = growFactor;

        clear();
    }

    public void clear()
    {
        m_Strings = null;
        m_Size = m_InitialSize;
        m_NumberOfStrings = 0;
        m_Strings = new String[m_InitialSize];
    }

    public void add(String string)
    {
        assert m_Strings != null;
        assert string != null;

        ++m_NumberOfStrings;

        if (m_NumberOfStrings > m_Size)
        {
            m_Size = m_Size * m_GrowFactor;
            String newStrings[] = new String[m_Size];

            for (int i = 0; i < m_NumberOfStrings - 1; ++i)
                newStrings[i] = m_Strings[i];

            m_Strings = newStrings;
        }

        m_Strings[m_NumberOfStrings - 1] = string;
    }

    public String[] getStringArray()
    {
        assert m_Strings != null;

        String stringArray[] = new String[m_NumberOfStrings];
        System.arraycopy(m_Strings, 0, stringArray, 0, m_NumberOfStrings);
        return stringArray;
    }

    public int getNumberOfStrings()
    {
        return m_NumberOfStrings;
    }
}
