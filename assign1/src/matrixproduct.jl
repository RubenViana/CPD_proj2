using Printf

function OnMult(m_ar::Int, m_br::Int)
    pha=zeros(m_ar*m_ar)
    phb=zeros(m_ar*m_ar)
    phc=zeros(m_ar*m_ar)

    st = ""

    for i in 1:m_ar
        for j in 1:m_ar
            pha[(i-1)*m_ar+j] = 1.0
        end
    end

    for i in 1:m_br
        for j in 1:m_br
            phb[(i-1)*m_br+j] = i
        end
    end

    Time1 = time()
    
    for i in 1:m_ar
        for j in 1:m_br
            temp = 0
            for k in 1:m_ar
                temp += pha[(i-1)*m_ar+k] * phb[(k-1)*m_br+j]
            end
            phc[(i-1)*m_br+j] = temp
        end
    end

    Time2 = time()
    ElapsedTime = Time2 - Time1

    st = @sprintf("Time: %3.3f seconds\n", ElapsedTime)
    println(st)

    println("Result Matrix: \n")
	for i in 1:1
        for j in 1:min(10, m_br)
			println(phc[j], " ")
        end
    end
	println("\n")

end



function OnMultLine(m_ar::Int, m_br::Int)
    pha=zeros(m_ar*m_ar)
    phb=zeros(m_ar*m_ar)
    phc=zeros(m_ar*m_ar)

    st = ""

    for i in 1:m_ar
        for j in 1:m_ar
            pha[(i-1)*m_ar+j] = 1.0
        end
    end

    for i in 1:m_br
        for j in 1:m_br
            phb[(i-1)*m_br+j] = i
        end
    end

    Time1 = time()
    
    for i in 1:m_ar
        for k in 1:m_ar
            temp = 0
            for j in 1:m_br
                phc[(i-1)*m_ar+j] += pha[(i-1)*m_ar+k] * phb[(k-1)*m_br+j]
            end
        end
    end

    Time2 = time()
    ElapsedTime = Time2 - Time1

    st = @sprintf("Time: %3.3f seconds\n", ElapsedTime)
    println(st)

    println("Result Matrix: \n")
	for i in 1:1
        for j in 1:min(10, m_br)
			println(phc[j], " ")
        end
    end
	println("\n")


end


function main()
    op = 1
    lin = 0
    col = 0
    blockSize = 0

    while true
        println("1. Multiplication\n")
        println("2. Line Multiplication\n")
        #println("3. Block Multiplication\n")
        println("Selection?: ")
        op = parse(Int, readline())
        println("Dimensions: lins=cols ? ")
        lin = parse(Int, readline())
        col = lin
    
        if op == 1
            OnMult(lin, col)
        elseif op == 2
            OnMultLine(lin, col)
        #elseif op == 3
        #    println("Block Size? ")
        #    blockSize = parse(Int, readline())
        #call OnMultBlock
        else
            break
        end
    end
end

main()